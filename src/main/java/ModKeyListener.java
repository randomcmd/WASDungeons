import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.awt.*;
import java.awt.event.InputEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ModKeyListener implements NativeKeyListener {

    public Boolean wPressed = false;
    public Boolean aPressed = false;
    public Boolean sPressed = false;
    public Boolean dPressed = false;
    public Boolean pause = false;

    //Resoulution changes automatically laterw
    public static int screenHeight = 1080;
    public static int screenWidth = 1920;

    public void nativeKeyPressed(NativeKeyEvent e) {

        System.out.println("Pressed " + NativeKeyEvent.getKeyText(e.getKeyCode()));

        if(NativeKeyEvent.getKeyText(e.getKeyCode()).equals("W")
            || NativeKeyEvent.getKeyText(e.getKeyCode()).equals("A")
                || NativeKeyEvent.getKeyText(e.getKeyCode()).equals("S")
                    || NativeKeyEvent.getKeyText(e.getKeyCode()).equals("D"))
        {
            try {
                robot = new Robot();
            } catch (AWTException awtException) {
                awtException.printStackTrace();
            }
            robot.mousePress(InputEvent.BUTTON1_MASK);
        }

        if(NativeKeyEvent.getKeyText(e.getKeyCode()).equals("P"))
        {
            pause = !pause;
        }

        if(NativeKeyEvent.getKeyText(e.getKeyCode()).equals("W"))
        {
            //Pressed W
            wPressed = true;
        }

        if(NativeKeyEvent.getKeyText(e.getKeyCode()).equals("A"))
        {
            //Pressed A
            aPressed = true;
        }

        if(NativeKeyEvent.getKeyText(e.getKeyCode()).equals("S"))
        {
            //Pressed S
            sPressed = true;
        }

        if(NativeKeyEvent.getKeyText(e.getKeyCode()).equals("D"))
        {
            //Pressed D
            dPressed = true;
        }

        if (e.getKeyCode() == NativeKeyEvent.VC_ESCAPE) {
            try {
                GlobalScreen.unregisterNativeHook();
            } catch (NativeHookException nativeHookException) {
            }
        }

        try {
            doMouseInput();
        } catch (AWTException awtException) {

        }

    }

    public void nativeKeyReleased(NativeKeyEvent e) {

        System.out.println("Released " + NativeKeyEvent.getKeyText(e.getKeyCode()));

        if(NativeKeyEvent.getKeyText(e.getKeyCode()).equals("W"))
        {
            //Pressed W
            wPressed = false;
        }

        if(NativeKeyEvent.getKeyText(e.getKeyCode()).equals("A"))
        {
            //Pressed A
            aPressed = false;
        }

        if(NativeKeyEvent.getKeyText(e.getKeyCode()).equals("S"))
        {
            //Pressed S
            sPressed = false;
        }

        if(NativeKeyEvent.getKeyText(e.getKeyCode()).equals("D"))
        {
            //Pressed D
            dPressed = false;
        }

        if(!wPressed && !aPressed && !sPressed && !dPressed)
        {
            robot.mouseMove(screenWidth/2,screenHeight/2);
            robot.mouseRelease(InputEvent.BUTTON1_MASK);
        }

        try {
            doMouseInput();
        } catch (AWTException awtException) {
            awtException.printStackTrace();
        }
    }

    public void nativeKeyTyped(NativeKeyEvent e) {
        System.out.println("Key Typed: " + e.getKeyText(e.getKeyCode()));
    }

    Robot robot;
    Dimension screenSize;
    public static void main(String[] args) throws AWTException {

        // Get the logger for "org.jnativehook" and set the level to warning.
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.WARNING);

        // Don't forget to disable the parent handlers.
        logger.setUseParentHandlers(false);

        ModKeyListener modKeyListener = new ModKeyListener();
        modKeyListener.init();

        try {
            GlobalScreen.registerNativeHook();
        }
        catch (NativeHookException ex) {
            System.err.println("There was a problem registering the native hook.");
            System.err.println(ex.getMessage());

            System.exit(1);
        }
        GlobalScreen.addNativeKeyListener(new ModKeyListener());
    }

    public void init(){
        System.out.println("Initializing ModKeyListener");
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
        ModKeyListener.screenHeight = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
        ModKeyListener.screenWidth = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
    }

    Vector2D inputVector;
    public void doMouseInput() throws AWTException {
        if(pause) return;
        inputVector = generateInputVector();
        System.out.println("Moved mouse to X: " + inputVector.x + " Y:" + inputVector.y);
        robot = new Robot();
        robot.mouseMove( (int) inputVector.x, (int) inputVector.y);
    }

    int wInput;
    int aInput;
    int sInput;
    int dInput;

    public Vector2D generateInputVector()
    {
        //Turn boolean into integer
        wInput = wPressed ? (screenHeight/2) * -1 : 0;
        aInput = aPressed ? (screenWidth/2) * -1 : 0;
        sInput = sPressed ? (screenHeight/2) : 0;
        dInput = dPressed ? (screenWidth/2) : 0;

        //Turns key input into screen position
        //W plus A for example is the right upper corner of the screen
        Vector2D output = new Vector2D();

        output.x = screenWidth/2 + aInput + dInput;
        output.y = screenHeight/2 + wInput + sInput;

        return output;
    }
}