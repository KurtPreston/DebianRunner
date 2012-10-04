package org.cs523.debianrunner;

import java.io.Serializable;

public enum ColorModelEnum implements Serializable {
	C24bit, C256, C64, C8, C4, C2;

	public String toString() {
		switch (this) {
		case C24bit:
			return "24-bit color (4 bpp)";
		case C256:
			return "256 colors (1 bpp)";
		case C64:
			return "64 colors (1 bpp)";
		case C8:
			return "8 colors (1 bpp)";
		case C4:
			return "Greyscale (1 bpp)";
		case C2:
			return "Black & White (1 bpp)";
		default:
			return "256 colors (1 bpp)";
		}
	}
}
