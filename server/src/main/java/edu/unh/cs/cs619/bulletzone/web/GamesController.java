package edu.unh.cs.cs619.bulletzone.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;

import javax.servlet.http.HttpServletRequest;

import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.IllegalTransitionException;
import edu.unh.cs.cs619.bulletzone.model.LimitExceededException;
import edu.unh.cs.cs619.bulletzone.model.Tank;
import edu.unh.cs.cs619.bulletzone.model.TankDoesNotExistException;
import edu.unh.cs.cs619.bulletzone.repository.GameRepository;
import edu.unh.cs.cs619.bulletzone.util.BooleanWrapper;
import edu.unh.cs.cs619.bulletzone.util.GridWrapper;
import edu.unh.cs.cs619.bulletzone.util.LongWrapper;

/**
 * <h1> GamesController Class! </h1>
 * This class handles posts and gets of controls coming from client
 * @author given
 * @version 1.0
 * @since halloween
 */
@RestController
@RequestMapping(value = "/games")
class GamesController {

    private static final Logger log = LoggerFactory.getLogger(GamesController.class);

    private final GameRepository gameRepository;

    @Autowired
    public GamesController(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @RequestMapping(method = RequestMethod.POST, value = "{identifier}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    ResponseEntity<LongWrapper> join(@PathVariable int identifier, HttpServletRequest request) {
        Tank tank;
        try {
            tank = gameRepository.join(request.getRemoteAddr(), identifier);
            log.info("Player joined: tankId={} IP={} identifier={}", tank.getId(), request.getRemoteAddr(), identifier);

            return new ResponseEntity<LongWrapper>(
                    new LongWrapper(tank.getId()),
                    HttpStatus.CREATED
            );
        } catch (RestClientException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping(method = RequestMethod.GET, value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public
    @ResponseBody
    ResponseEntity<GridWrapper> grid() {
        return new ResponseEntity<GridWrapper>(new GridWrapper(gameRepository.getGrid()), HttpStatus.ACCEPTED);
    }

    /*
     *  whoAmI get request - added for testing, request provides ip of test client
     *  expects associated tankID from server.
     */
    @RequestMapping(method = RequestMethod.GET, value = "whoAmI", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public
    @ResponseBody
    ResponseEntity<LongWrapper> whoAmI(HttpServletRequest request) throws TankDoesNotExistException {
        Tank tank;
        try {
            tank = gameRepository.whoAmI(request.getRemoteAddr());
            log.info("Player requested: tankId={} IP={}", tank.getId(), request.getRemoteAddr());

            return new ResponseEntity<LongWrapper>(
                    new LongWrapper(tank.getId()),
                    HttpStatus.OK
            );
        } catch (RestClientException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping(method = RequestMethod.GET, value = "{primaryId}/amIAlive/{remoteId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public
    @ResponseBody
    ResponseEntity<LongWrapper> amIAlive(@PathVariable long primaryId, @PathVariable long remoteId)throws TankDoesNotExistException {
        long Alive = 0;
        try {
            Alive = gameRepository.amIAlive(primaryId, remoteId);
            //log.info("Player primary and remote alive: ", Alive);
            //System.out.println("amialive");
            return new ResponseEntity<LongWrapper>(
                    new LongWrapper(Alive),
                    HttpStatus.OK
            );
        } catch (RestClientException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping(method = RequestMethod.GET, value = "whereAmI", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public
    @ResponseBody
    ResponseEntity<LongWrapper> whereAmI(HttpServletRequest request) throws TankDoesNotExistException {
        int loc;
        try {
            loc = gameRepository.whereAmI(request.getRemoteAddr());
            log.info("Player requested: playerLoc={} IP={}", loc, request.getRemoteAddr());

            return new ResponseEntity<LongWrapper>(
                    new LongWrapper(loc),
                    HttpStatus.OK
            );
        } catch (RestClientException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping(method = RequestMethod.GET, value = "whichWay", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public
    @ResponseBody
    ResponseEntity<LongWrapper> whichWay(HttpServletRequest request) throws TankDoesNotExistException {
        byte dir;
        try {
            dir = gameRepository.whichWay(request.getRemoteAddr());
            log.info("Player requested: playerDir={} IP={}", dir, request.getRemoteAddr());

            return new ResponseEntity<LongWrapper>(
                    new LongWrapper(dir),
                    HttpStatus.OK
            );
        } catch (RestClientException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping(method = RequestMethod.GET, value = "howManyBullets", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public
    @ResponseBody
    ResponseEntity<LongWrapper> howManyBullets(HttpServletRequest request) throws TankDoesNotExistException {
        int bullets;
        try {
            bullets = gameRepository.howManyBullets(request.getRemoteAddr());
            log.info("Player requested: playerAmmo={} IP={}", bullets, request.getRemoteAddr());

            return new ResponseEntity<LongWrapper>(
                    new LongWrapper(bullets),
                    HttpStatus.OK
            );
        } catch (RestClientException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping(method = RequestMethod.PUT, value = "{tankId}/turn/{direction}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    ResponseEntity<BooleanWrapper> turn(@PathVariable long tankId, @PathVariable byte direction)
            throws TankDoesNotExistException, LimitExceededException, IllegalTransitionException {
        return new ResponseEntity<BooleanWrapper>(
                new BooleanWrapper(gameRepository.turn(tankId, Direction.fromByte(direction))),
                HttpStatus.ACCEPTED
        );
    }

    @RequestMapping(method = RequestMethod.PUT, value = "{tankId}/move/{direction}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    ResponseEntity<BooleanWrapper> move(@PathVariable long tankId, @PathVariable byte direction)
            throws TankDoesNotExistException, LimitExceededException, IllegalTransitionException {
        return new ResponseEntity<BooleanWrapper>(
                new BooleanWrapper(gameRepository.move(tankId, Direction.fromByte(direction))),
                HttpStatus.ACCEPTED
        );
    }

    @RequestMapping(method = RequestMethod.PUT, value = "{tankId}/fire/{direction}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    ResponseEntity<BooleanWrapper> fire(@PathVariable long tankId, @PathVariable byte direction)
            throws TankDoesNotExistException, LimitExceededException {
        return new ResponseEntity<BooleanWrapper>(
                new BooleanWrapper(gameRepository.fire(tankId, Direction.fromByte(direction))),
                HttpStatus.ACCEPTED
        );
    }

    @RequestMapping(method = RequestMethod.PUT, value = "{tankId}/eject/1", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    HttpStatus eject(@PathVariable long tankId)
            throws TankDoesNotExistException, LimitExceededException {
        gameRepository.eject(tankId);
        return HttpStatus.ACCEPTED;
    }

    @RequestMapping(method = RequestMethod.PUT, value = "{tankId}/ejectSoldier/1", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    HttpStatus ejectSoldier(@PathVariable long tankId)
            throws TankDoesNotExistException{
        gameRepository.ejectSoldier(tankId);
        return HttpStatus.ACCEPTED;
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "{tankId}/leave", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    HttpStatus leave(@PathVariable long tankId)
            throws TankDoesNotExistException {
        //System.out.println("Games Controller leave() called, tank ID: "+tankId);
        gameRepository.leave(tankId);
        return HttpStatus.ACCEPTED;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String handleBadRequests(Exception e) {
        return e.getMessage();
    }
}