package calculatorGUI.state;

public enum PanelMode { 
	MODE00,
	MODE01;
	
	public PanelMode next() {
        PanelMode[] values = PanelMode.values();
        return values[(this.ordinal() + 1) % values.length];
    }
}
