package calculatorGUI.state;

public enum AngleMode { 
		RAD, DEG, GRAD;
		
	public AngleMode next() {
		AngleMode[] values = AngleMode.values();
        return values[(this.ordinal() + 1) % values.length];
    }
}
