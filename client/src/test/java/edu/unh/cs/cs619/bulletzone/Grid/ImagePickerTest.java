package edu.unh.cs.cs619.bulletzone.Grid;

import org.junit.Test;

import edu.unh.cs.cs619.bulletzone.Grid.ImagePicker;
import edu.unh.cs.cs619.bulletzone.R;

public class ImagePickerTest {
    private ImagePicker mImagePicker = new ImagePicker();
    private int resultImage = 0;

    @Test
    public void pickImage_BlankImage_ReturnsBlank() {
        resultImage = mImagePicker.pickImage(0);
        assert(resultImage == R.drawable.blank);
    }
    @Test
    public void pickImage_IndestructibleWall_ReturnsIndestructible() {
        resultImage = mImagePicker.pickImage(1000);
        assert(resultImage == R.drawable.indestructible_wall);
    }
    @Test
    public void pickImage_DestructibleWall_ReturnsDestructible() {
        resultImage = mImagePicker.pickImage(1503);
        assert(resultImage == R.drawable.destructible_wall_full);
    }
    @Test
    public void pickImage_Bullet_ReturnsBullet() {
        resultImage = mImagePicker.pickImage(2500000);
        assert(resultImage == R.drawable.bullet_up);
    }
    @Test
    public void pickImage_EnemyTank_ReturnsEnemyTank() {
        resultImage = mImagePicker.pickImage(10120030);
        assert(resultImage == R.drawable.tank2_up_low);
    }
}