package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.Inventory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class InventoryTest {

    Inventory inventory;
    String[] gadgets;

    @BeforeEach
    public void setUp(){
        inventory = Inventory.getInstance();
        String[] gadgets = {"a", "b", "aa", "t", "T", "0"};
        inventory.load(gadgets);
    }

    @Test
    public void testGetItem(){
        for(int i = 0; i < gadgets.length; i++){
            assertTrue(inventory.getItem(gadgets[i]), "Not recognizing existing gadget");
        }

        assertFalse(inventory.getItem("z"),"Recognizing not existing gadget");
        assertFalse(inventory.getItem("9"),"Recognizing not existing gadget");
        assertFalse(inventory.getItem(""),"Recognizing not existing gadget");
    }

    @Test
    public void testPrintToFile() throws FileNotFoundException {
        File output = new File("testOutput.txt");
        int gadgetIndex = 0;
        try{
            output.createNewFile();
        } catch(Exception e){}

        Scanner scanner = new Scanner(output);
        inventory.printToFile("testOutput.txt");

        while(scanner.hasNext()){
            if(gadgetIndex >= gadgets.length) fail("Too many gadgets written");
            else assertEquals(scanner.next(), gadgets[gadgetIndex]);
        }
    }


}
