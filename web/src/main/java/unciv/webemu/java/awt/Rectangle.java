package unciv.webemu.java.awt;

/** Just the fields of java.awt.Rectangle, for GameSettings.WindowState's desktop-only constructor. */
public class Rectangle {
    public int x;
    public int y;
    public int width;
    public int height;

    public Rectangle() {}

    public Rectangle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
}
