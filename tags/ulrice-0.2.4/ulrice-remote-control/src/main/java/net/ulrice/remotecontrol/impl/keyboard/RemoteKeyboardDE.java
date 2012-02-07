package net.ulrice.remotecontrol.impl.keyboard;

import static java.awt.event.KeyEvent.*;
import static net.ulrice.remotecontrol.impl.keyboard.RemoteKeyboardInstruction.*;

public class RemoteKeyboardDE extends RemoteKeyboard
{

	public static final RemoteKeyboard INSTANCE = new RemoteKeyboardDE();

	protected RemoteKeyboardDE()
	{
		super();

		register('^', press(VK_DEAD_CIRCUMFLEX), release(VK_DEAD_CIRCUMFLEX), press(VK_SPACE), release(VK_SPACE));
		register('\u00b0', press(VK_SHIFT), press(VK_DEAD_CIRCUMFLEX), release(VK_DEAD_CIRCUMFLEX), release(VK_SHIFT));
		register('!', press(VK_SHIFT), press(VK_1), release(VK_1), release(VK_SHIFT));
		register('\"', press(VK_SHIFT), press(VK_2), release(VK_2), release(VK_SHIFT));
		register('\u00b2', press(VK_CONTROL), press(VK_ALT), press(VK_2), release(VK_2), release(VK_CONTROL),
		    release(VK_ALT));
		register('\u00a7', press(VK_SHIFT), press(VK_3), release(VK_3), release(VK_SHIFT));
		register('\u00b3', press(VK_CONTROL), press(VK_ALT), press(VK_3), release(VK_3), release(VK_CONTROL),
		    release(VK_ALT));
		register('$', press(VK_SHIFT), press(VK_4), release(VK_4), release(VK_SHIFT));
		register('%', press(VK_SHIFT), press(VK_5), release(VK_5), release(VK_SHIFT));
		register('&', press(VK_SHIFT), press(VK_6), release(VK_6), release(VK_SHIFT));
		register('/', press(VK_SHIFT), press(VK_7), release(VK_7), release(VK_SHIFT));
		register('{', press(VK_CONTROL), press(VK_ALT), press(VK_7), release(VK_7), release(VK_CONTROL),
		    release(VK_ALT));
		register('(', press(VK_SHIFT), press(VK_8), release(VK_8), release(VK_SHIFT));
		register('[', press(VK_CONTROL), press(VK_ALT), press(VK_8), release(VK_8), release(VK_CONTROL),
		    release(VK_ALT));
		register(')', press(VK_SHIFT), press(VK_9), release(VK_9), release(VK_SHIFT));
		register(']', press(VK_CONTROL), press(VK_ALT), press(VK_9), release(VK_9), release(VK_CONTROL),
		    release(VK_ALT));
		register('=', press(VK_SHIFT), press(VK_0), release(VK_0), release(VK_SHIFT));
		register('}', press(VK_CONTROL), press(VK_ALT), press(VK_0), release(VK_0), release(VK_CONTROL),
		    release(VK_ALT));
		register('\u00b4', press(VK_DEAD_ACUTE), release(VK_DEAD_ACUTE), press(VK_SPACE), release(VK_SPACE));
		register('`', press(VK_SHIFT), press(VK_DEAD_ACUTE), release(VK_DEAD_ACUTE), release(VK_SHIFT),
		    press(VK_SPACE), release(VK_SPACE));
		register('\b', press(VK_BACK_SPACE), release(VK_BACK_SPACE));
		register('@', press(VK_CONTROL), press(VK_ALT), press(VK_Q), release(VK_Q), release(VK_CONTROL),
		    release(VK_ALT));
		register('\u20ac', press(VK_CONTROL), press(VK_ALT), press(VK_E), release(VK_E), release(VK_CONTROL),
		    release(VK_ALT));
		register('+', press(VK_PLUS), release(VK_PLUS));
		register('*', press(VK_SHIFT), press(VK_PLUS), release(VK_PLUS), release(VK_SHIFT));
		register('~', press(VK_CONTROL), press(VK_ALT), press(VK_PLUS), release(VK_PLUS), release(VK_CONTROL),
		    release(VK_ALT));
		register('#', press(VK_NUMBER_SIGN), release(VK_NUMBER_SIGN));
		register('\'', press(VK_SHIFT), press(VK_NUMBER_SIGN), release(VK_SHIFT), release(VK_NUMBER_SIGN));
		register('<', press(VK_LESS), release(VK_LESS));
		register('>', press(VK_SHIFT), press(VK_LESS), release(VK_LESS), release(VK_SHIFT));
		register('|', press(VK_CONTROL), press(VK_ALT), press(VK_LESS), release(VK_LESS), release(VK_CONTROL),
		    release(VK_ALT));
		register(',', press(VK_COMMA), release(VK_COMMA));
		register(';', press(VK_SHIFT), press(VK_COMMA), release(VK_COMMA), release(VK_SHIFT));
		register('.', press(VK_PERIOD), release(VK_PERIOD));
		register(':', press(VK_SHIFT), press(VK_PERIOD), release(VK_PERIOD), release(VK_SHIFT));
		register('-', press(VK_MINUS), release(VK_MINUS));
		register('_', press(VK_SHIFT), press(VK_MINUS), release(VK_MINUS), release(VK_SHIFT));

		register('\u00e2', press(VK_DEAD_CIRCUMFLEX), release(VK_DEAD_CIRCUMFLEX), press(VK_A), release(VK_A));
		register('\u00ea', press(VK_DEAD_CIRCUMFLEX), release(VK_DEAD_CIRCUMFLEX), press(VK_E), release(VK_E));
		register('\u00ee', press(VK_DEAD_CIRCUMFLEX), release(VK_DEAD_CIRCUMFLEX), press(VK_I), release(VK_I));
		register('\u00f4', press(VK_DEAD_CIRCUMFLEX), release(VK_DEAD_CIRCUMFLEX), press(VK_O), release(VK_O));
		register('\u00fb', press(VK_DEAD_CIRCUMFLEX), release(VK_DEAD_CIRCUMFLEX), press(VK_U), release(VK_U));
		register('\u00e1', press(VK_DEAD_ACUTE), release(VK_DEAD_ACUTE), press(VK_A), release(VK_A));
		register('\u00e9', press(VK_DEAD_ACUTE), release(VK_DEAD_ACUTE), press(VK_E), release(VK_E));
		register('\u00ed', press(VK_DEAD_ACUTE), release(VK_DEAD_ACUTE), press(VK_I), release(VK_I));
		register('\u00f3', press(VK_DEAD_ACUTE), release(VK_DEAD_ACUTE), press(VK_O), release(VK_O));
		register('\u00fa', press(VK_DEAD_ACUTE), release(VK_DEAD_ACUTE), press(VK_U), release(VK_U));
		register('\u00e0', press(VK_SHIFT), press(VK_DEAD_ACUTE), release(VK_DEAD_ACUTE), release(VK_SHIFT),
		    press(VK_A), release(VK_A));
		register('\u00e8', press(VK_SHIFT), press(VK_DEAD_ACUTE), release(VK_DEAD_ACUTE), release(VK_SHIFT),
		    press(VK_E), release(VK_E));
		register('\u00f2', press(VK_SHIFT), press(VK_DEAD_ACUTE), release(VK_DEAD_ACUTE), release(VK_SHIFT),
		    press(VK_O), release(VK_O));
		register('\u00ec', press(VK_SHIFT), press(VK_DEAD_ACUTE), release(VK_DEAD_ACUTE), release(VK_SHIFT),
		    press(VK_I), release(VK_I));
		register('\u00f9', press(VK_SHIFT), press(VK_DEAD_ACUTE), release(VK_DEAD_ACUTE), release(VK_SHIFT),
		    press(VK_U), release(VK_U));
		register('\u00b5', press(VK_CONTROL), press(VK_ALT), press(VK_M), release(VK_M), release(VK_CONTROL),
		    release(VK_ALT));
		register('\u00c0', press(VK_SHIFT), press(VK_DEAD_ACUTE), release(VK_DEAD_ACUTE), press(VK_A), release(VK_A),
		    release(VK_SHIFT));
		register('\u00c1', press(VK_DEAD_ACUTE), release(VK_DEAD_ACUTE), press(VK_SHIFT), press(VK_A), release(VK_A),
		    release(VK_SHIFT));
		register('\u00c2', press(VK_DEAD_CIRCUMFLEX), release(VK_DEAD_CIRCUMFLEX), press(VK_SHIFT), press(VK_A),
		    release(VK_A), release(VK_SHIFT));
		register('\u00c8', press(VK_SHIFT), press(VK_DEAD_ACUTE), release(VK_DEAD_ACUTE), press(VK_E), release(VK_E),
		    release(VK_SHIFT));
		register('\u00c9', press(VK_DEAD_ACUTE), release(VK_DEAD_ACUTE), press(VK_SHIFT), press(VK_E), release(VK_E),
		    release(VK_SHIFT));
		register('\u00ca', press(VK_DEAD_CIRCUMFLEX), release(VK_DEAD_CIRCUMFLEX), press(VK_SHIFT), press(VK_E),
		    release(VK_E), release(VK_SHIFT));
		register('\u00cc', press(VK_SHIFT), press(VK_DEAD_ACUTE), release(VK_DEAD_ACUTE), press(VK_I), release(VK_I),
		    release(VK_SHIFT));
		register('\u00cd', press(VK_DEAD_ACUTE), release(VK_DEAD_ACUTE), press(VK_SHIFT), press(VK_I), release(VK_I),
		    release(VK_SHIFT));
		register('\u00ce', press(VK_DEAD_CIRCUMFLEX), release(VK_DEAD_CIRCUMFLEX), press(VK_SHIFT), press(VK_I),
		    release(VK_I), release(VK_SHIFT));
		register('\u00d2', press(VK_SHIFT), press(VK_DEAD_ACUTE), release(VK_DEAD_ACUTE), press(VK_O), release(VK_O),
		    release(VK_SHIFT));
		register('\u00d3', press(VK_DEAD_ACUTE), release(VK_DEAD_ACUTE), press(VK_SHIFT), press(VK_O), release(VK_O),
		    release(VK_SHIFT));
		register('\u00d4', press(VK_DEAD_CIRCUMFLEX), release(VK_DEAD_CIRCUMFLEX), press(VK_SHIFT), press(VK_O),
		    release(VK_O), release(VK_SHIFT));
		register('\u00d9', press(VK_SHIFT), press(VK_DEAD_ACUTE), release(VK_DEAD_ACUTE), press(VK_U), release(VK_U),
		    release(VK_SHIFT));
		register('\u00da', press(VK_DEAD_ACUTE), release(VK_DEAD_ACUTE), press(VK_SHIFT), press(VK_U), release(VK_U),
		    release(VK_SHIFT));
		register('\u00db', press(VK_DEAD_CIRCUMFLEX), release(VK_DEAD_CIRCUMFLEX), press(VK_SHIFT), press(VK_U),
		    release(VK_U), release(VK_SHIFT));
		register('\u00dd', press(VK_DEAD_ACUTE), release(VK_DEAD_ACUTE), press(VK_SHIFT), press(VK_Y), release(VK_Y),
		    release(VK_SHIFT));
		register('\u00fd', press(VK_DEAD_ACUTE), release(VK_DEAD_ACUTE), press(VK_Y), release(VK_Y));

		// workarounds for missing German keys
		register('\u00c4', press(VK_ALT), press(VK_NUMPAD1), release(VK_NUMPAD1), press(VK_NUMPAD4),
		    release(VK_NUMPAD4), press(VK_NUMPAD2), release(VK_NUMPAD2), release(VK_ALT));
		register('\u00d6', press(VK_ALT), press(VK_NUMPAD1), release(VK_NUMPAD1), press(VK_NUMPAD5),
		    release(VK_NUMPAD5), press(VK_NUMPAD3), release(VK_NUMPAD3), release(VK_ALT));
		register('\u00dc', press(VK_ALT), press(VK_NUMPAD2), release(VK_NUMPAD2), press(VK_NUMPAD2),
		    release(VK_NUMPAD1), press(VK_NUMPAD0), release(VK_NUMPAD0), release(VK_ALT));
		register('\u00df', press(VK_ALT), press(VK_NUMPAD1), release(VK_NUMPAD1), press(VK_NUMPAD5),
		    release(VK_NUMPAD5), press(VK_NUMPAD4), release(VK_NUMPAD4), release(VK_ALT));
		register('\u00e4', press(VK_ALT), press(VK_NUMPAD1), release(VK_NUMPAD1), press(VK_NUMPAD3),
		    release(VK_NUMPAD3), press(VK_NUMPAD2), release(VK_NUMPAD2), release(VK_ALT));
		register('\u00f6', press(VK_ALT), press(VK_NUMPAD1), release(VK_NUMPAD1), press(VK_NUMPAD4),
		    release(VK_NUMPAD4), press(VK_NUMPAD8), release(VK_NUMPAD8), release(VK_ALT));
		register('\u00fc', press(VK_ALT), press(VK_NUMPAD1), release(VK_NUMPAD1), press(VK_NUMPAD2),
		    release(VK_NUMPAD2), press(VK_NUMPAD9), release(VK_NUMPAD9), release(VK_ALT));

		register('`', press(VK_ALT), press(VK_NUMPAD2), release(VK_NUMPAD2), press(VK_NUMPAD2), release(VK_NUMPAD2),
		    press(VK_NUMPAD3), release(VK_NUMPAD3), release(VK_ALT));
		register('?', press(VK_ALT), press(VK_NUMPAD6), release(VK_NUMPAD6), press(VK_NUMPAD3), release(VK_NUMPAD3),
		    release(VK_ALT));
		register('\\', press(VK_ALT), press(VK_NUMPAD9), release(VK_NUMPAD9), press(VK_NUMPAD2), release(VK_NUMPAD2),
		    release(VK_ALT));

	}

}
