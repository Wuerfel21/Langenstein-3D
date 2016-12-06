package net.irq_interactive.langenstein3D;

import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.OceanTheme;

public class Puke extends DefaultMetalTheme {

    public String getName() { return "Puke"; }

    private final ColorUIResource primary1 = new ColorUIResource(51, 142, 71);
    private final ColorUIResource primary2 = new ColorUIResource(102, 193, 122);
    private final ColorUIResource primary3 = new ColorUIResource(153, 244, 173); 

    private final ColorUIResource secondary1 = new ColorUIResource(42,42,42);
    private final ColorUIResource secondary2 = new ColorUIResource(0xCF,0xD7,0xB6);//(0xB8,0xCF,0xE5);
    private final ColorUIResource secondary3 = new ColorUIResource(0xB2,0xBA,0x8A);
    
    private final ColorUIResource desktopColor = new ColorUIResource(0x30,0x38,0x20);
    private final ColorUIResource white = new ColorUIResource(0xAA,0xB6,0x82);
    

    @Override
    protected ColorUIResource getPrimary1() { return primary1; }
    @Override
    protected ColorUIResource getPrimary2() { return primary2; }
    @Override
    protected ColorUIResource getPrimary3() { return primary3; }

    @Override
    protected ColorUIResource getSecondary1() { return secondary1; }
    @Override
    protected ColorUIResource getSecondary2() { return secondary2; }
    @Override
    protected ColorUIResource getSecondary3() { return secondary3; }
    
    @Override
	public ColorUIResource getDesktopColor() {return desktopColor;}
    @Override
   	public ColorUIResource getWhite() {return white;}

}
