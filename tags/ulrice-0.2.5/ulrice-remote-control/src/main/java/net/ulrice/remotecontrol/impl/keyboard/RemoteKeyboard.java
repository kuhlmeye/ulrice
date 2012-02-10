package net.ulrice.remotecontrol.impl.keyboard;

import static java.awt.event.KeyEvent.*;
import static net.ulrice.remotecontrol.impl.keyboard.RemoteKeyboardInstruction.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public abstract class RemoteKeyboard
{

	private static Map<String, RemoteKeyboardInstruction[]> DEFAULT_MAPPING_BY_CODE =
	    new HashMap<String, RemoteKeyboardInstruction[]>();
	private static Map<Character, RemoteKeyboardInstruction[]> DEFAULT_MAPPING_BY_KEY =
	    new HashMap<Character, RemoteKeyboardInstruction[]>();

	static
	{
		DEFAULT_MAPPING_BY_CODE.put("enter", new RemoteKeyboardInstruction[] {
		    press(VK_ENTER), release(VK_ENTER)
		});
		DEFAULT_MAPPING_BY_CODE.put("backspace", new RemoteKeyboardInstruction[] {
		    press(VK_BACK_SPACE), release(VK_BACK_SPACE)
		});
		DEFAULT_MAPPING_BY_CODE.put("tab", new RemoteKeyboardInstruction[] {
		    press(VK_TAB), release(VK_TAB)
		});
		DEFAULT_MAPPING_BY_CODE.put("cancel", new RemoteKeyboardInstruction[] {
		    press(VK_CANCEL), release(VK_CANCEL)
		});
		DEFAULT_MAPPING_BY_CODE.put("clear", new RemoteKeyboardInstruction[] {
		    press(VK_CLEAR), release(VK_CLEAR)
		});
		DEFAULT_MAPPING_BY_CODE.put("compose", new RemoteKeyboardInstruction[] {
		    press(VK_COMPOSE), release(VK_COMPOSE)
		});
		DEFAULT_MAPPING_BY_CODE.put("pause", new RemoteKeyboardInstruction[] {
		    press(VK_PAUSE), release(VK_PAUSE)
		});
		DEFAULT_MAPPING_BY_CODE.put("caps lock", new RemoteKeyboardInstruction[] {
		    press(VK_CAPS_LOCK), release(VK_CAPS_LOCK)
		});
		DEFAULT_MAPPING_BY_CODE.put("escape", new RemoteKeyboardInstruction[] {
		    press(VK_ESCAPE), release(VK_ESCAPE)
		});
		DEFAULT_MAPPING_BY_CODE.put("space", new RemoteKeyboardInstruction[] {
		    press(VK_SPACE), release(VK_SPACE)
		});
		DEFAULT_MAPPING_BY_CODE.put("page up", new RemoteKeyboardInstruction[] {
		    press(VK_PAGE_UP), release(VK_PAGE_UP)
		});
		DEFAULT_MAPPING_BY_CODE.put("page down", new RemoteKeyboardInstruction[] {
		    press(VK_PAGE_DOWN), release(VK_PAGE_DOWN)
		});
		DEFAULT_MAPPING_BY_CODE.put("end", new RemoteKeyboardInstruction[] {
		    press(VK_END), release(VK_END)
		});
		DEFAULT_MAPPING_BY_CODE.put("home", new RemoteKeyboardInstruction[] {
		    press(VK_HOME), release(VK_HOME)
		});
		DEFAULT_MAPPING_BY_CODE.put("left", new RemoteKeyboardInstruction[] {
		    press(VK_LEFT), release(VK_LEFT)
		});
		DEFAULT_MAPPING_BY_CODE.put("up", new RemoteKeyboardInstruction[] {
		    press(VK_UP), release(VK_UP)
		});
		DEFAULT_MAPPING_BY_CODE.put("right", new RemoteKeyboardInstruction[] {
		    press(VK_RIGHT), release(VK_RIGHT)
		});
		DEFAULT_MAPPING_BY_CODE.put("down", new RemoteKeyboardInstruction[] {
		    press(VK_DOWN), release(VK_DOWN)
		});
		DEFAULT_MAPPING_BY_CODE.put("begin", new RemoteKeyboardInstruction[] {
		    press(VK_BEGIN), release(VK_BEGIN)
		});

		DEFAULT_MAPPING_BY_CODE.put("shift", new RemoteKeyboardInstruction[] {
		    press(VK_SHIFT), release(VK_SHIFT)
		});
		DEFAULT_MAPPING_BY_CODE.put("control", new RemoteKeyboardInstruction[] {
		    press(VK_CONTROL), release(VK_CONTROL)
		});
		DEFAULT_MAPPING_BY_CODE.put("ctrl", new RemoteKeyboardInstruction[] {
		    press(VK_CONTROL), release(VK_CONTROL)
		});
		DEFAULT_MAPPING_BY_CODE.put("alt", new RemoteKeyboardInstruction[] {
		    press(VK_ALT), release(VK_ALT)
		});
		DEFAULT_MAPPING_BY_CODE.put("meta", new RemoteKeyboardInstruction[] {
		    press(VK_META), release(VK_META)
		});
		DEFAULT_MAPPING_BY_CODE.put("alt graph", new RemoteKeyboardInstruction[] {
		    press(VK_ALT_GRAPH), release(VK_ALT_GRAPH)
		});
		DEFAULT_MAPPING_BY_CODE.put("alt gr", new RemoteKeyboardInstruction[] {
		    press(VK_ALT_GRAPH), release(VK_ALT_GRAPH)
		});
		DEFAULT_MAPPING_BY_CODE.put("altgr", new RemoteKeyboardInstruction[] {
		    press(VK_ALT_GRAPH), release(VK_ALT_GRAPH)
		});

		DEFAULT_MAPPING_BY_CODE.put("comma", new RemoteKeyboardInstruction[] {
		    press(VK_COMMA), release(VK_COMMA)
		});
		DEFAULT_MAPPING_BY_CODE.put("period", new RemoteKeyboardInstruction[] {
		    press(VK_PERIOD), release(VK_PERIOD)
		});
		DEFAULT_MAPPING_BY_CODE.put("slash", new RemoteKeyboardInstruction[] {
		    press(VK_SLASH), release(VK_SLASH)
		});
		DEFAULT_MAPPING_BY_CODE.put("semicolon", new RemoteKeyboardInstruction[] {
		    press(VK_SEMICOLON), release(VK_SEMICOLON)
		});
		DEFAULT_MAPPING_BY_CODE.put("equals", new RemoteKeyboardInstruction[] {
		    press(VK_EQUALS), release(VK_EQUALS)
		});
		DEFAULT_MAPPING_BY_CODE.put("open bracket", new RemoteKeyboardInstruction[] {
		    press(VK_OPEN_BRACKET), release(VK_OPEN_BRACKET)
		});
		DEFAULT_MAPPING_BY_CODE.put("back slash", new RemoteKeyboardInstruction[] {
		    press(VK_BACK_SLASH), release(VK_BACK_SLASH)
		});
		DEFAULT_MAPPING_BY_CODE.put("close bracket", new RemoteKeyboardInstruction[] {
		    press(VK_CLOSE_BRACKET), release(VK_CLOSE_BRACKET)
		});

		DEFAULT_MAPPING_BY_CODE.put("numpad *", new RemoteKeyboardInstruction[] {
		    press(VK_MULTIPLY), release(VK_MULTIPLY)
		});
		DEFAULT_MAPPING_BY_CODE.put("numpad +", new RemoteKeyboardInstruction[] {
		    press(VK_ADD), release(VK_ADD)
		});
		DEFAULT_MAPPING_BY_CODE.put("numpad ,", new RemoteKeyboardInstruction[] {
		    press(VK_SEPARATOR), release(VK_SEPARATOR)
		});
		DEFAULT_MAPPING_BY_CODE.put("numpad -", new RemoteKeyboardInstruction[] {
		    press(VK_SUBTRACT), release(VK_SUBTRACT)
		});
		DEFAULT_MAPPING_BY_CODE.put("numpad .", new RemoteKeyboardInstruction[] {
		    press(VK_DECIMAL), release(VK_DECIMAL)
		});
		DEFAULT_MAPPING_BY_CODE.put("numpad /", new RemoteKeyboardInstruction[] {
		    press(VK_DIVIDE), release(VK_DIVIDE)
		});

		DEFAULT_MAPPING_BY_CODE.put("numpad 0", new RemoteKeyboardInstruction[] {
		    press(VK_NUMPAD0), release(VK_NUMPAD0)
		});
		DEFAULT_MAPPING_BY_CODE.put("numpad 1", new RemoteKeyboardInstruction[] {
		    press(VK_NUMPAD1), release(VK_NUMPAD1)
		});
		DEFAULT_MAPPING_BY_CODE.put("numpad 2", new RemoteKeyboardInstruction[] {
		    press(VK_NUMPAD2), release(VK_NUMPAD2)
		});
		DEFAULT_MAPPING_BY_CODE.put("numpad 3", new RemoteKeyboardInstruction[] {
		    press(VK_NUMPAD3), release(VK_NUMPAD3)
		});
		DEFAULT_MAPPING_BY_CODE.put("numpad 4", new RemoteKeyboardInstruction[] {
		    press(VK_NUMPAD4), release(VK_NUMPAD4)
		});
		DEFAULT_MAPPING_BY_CODE.put("numpad 5", new RemoteKeyboardInstruction[] {
		    press(VK_NUMPAD5), release(VK_NUMPAD5)
		});
		DEFAULT_MAPPING_BY_CODE.put("numpad 6", new RemoteKeyboardInstruction[] {
		    press(VK_NUMPAD6), release(VK_NUMPAD6)
		});
		DEFAULT_MAPPING_BY_CODE.put("numpad 7", new RemoteKeyboardInstruction[] {
		    press(VK_NUMPAD7), release(VK_NUMPAD7)
		});
		DEFAULT_MAPPING_BY_CODE.put("numpad 8", new RemoteKeyboardInstruction[] {
		    press(VK_NUMPAD8), release(VK_NUMPAD8)
		});
		DEFAULT_MAPPING_BY_CODE.put("numpad 9", new RemoteKeyboardInstruction[] {
		    press(VK_NUMPAD9), release(VK_NUMPAD9)
		});

		DEFAULT_MAPPING_BY_CODE.put("delete", new RemoteKeyboardInstruction[] {
		    press(VK_DELETE), release(VK_DELETE)
		});
		DEFAULT_MAPPING_BY_CODE.put("num lock", new RemoteKeyboardInstruction[] {
		    press(VK_NUM_LOCK), release(VK_NUM_LOCK)
		});
		DEFAULT_MAPPING_BY_CODE.put("scroll lock", new RemoteKeyboardInstruction[] {
		    press(VK_SCROLL_LOCK), release(VK_SCROLL_LOCK)
		});

		DEFAULT_MAPPING_BY_CODE.put("windows", new RemoteKeyboardInstruction[] {
		    press(VK_WINDOWS), release(VK_WINDOWS)
		});
		DEFAULT_MAPPING_BY_CODE.put("win", new RemoteKeyboardInstruction[] {
		    press(VK_WINDOWS), release(VK_WINDOWS)
		});
		DEFAULT_MAPPING_BY_CODE.put("context menu", new RemoteKeyboardInstruction[] {
		    press(VK_CONTEXT_MENU), release(VK_CONTEXT_MENU)
		});

		DEFAULT_MAPPING_BY_CODE.put("f1", new RemoteKeyboardInstruction[] {
		    press(VK_F1), release(VK_F1)
		});
		DEFAULT_MAPPING_BY_CODE.put("f2", new RemoteKeyboardInstruction[] {
		    press(VK_F2), release(VK_F2)
		});
		DEFAULT_MAPPING_BY_CODE.put("f3", new RemoteKeyboardInstruction[] {
		    press(VK_F3), release(VK_F3)
		});
		DEFAULT_MAPPING_BY_CODE.put("f4", new RemoteKeyboardInstruction[] {
		    press(VK_F4), release(VK_F4)
		});
		DEFAULT_MAPPING_BY_CODE.put("f5", new RemoteKeyboardInstruction[] {
		    press(VK_F5), release(VK_F5)
		});
		DEFAULT_MAPPING_BY_CODE.put("f6", new RemoteKeyboardInstruction[] {
		    press(VK_F6), release(VK_F6)
		});
		DEFAULT_MAPPING_BY_CODE.put("f7", new RemoteKeyboardInstruction[] {
		    press(VK_F7), release(VK_F7)
		});
		DEFAULT_MAPPING_BY_CODE.put("f8", new RemoteKeyboardInstruction[] {
		    press(VK_F8), release(VK_F8)
		});
		DEFAULT_MAPPING_BY_CODE.put("f9", new RemoteKeyboardInstruction[] {
		    press(VK_F9), release(VK_F9)
		});
		DEFAULT_MAPPING_BY_CODE.put("f10", new RemoteKeyboardInstruction[] {
		    press(VK_F10), release(VK_F10)
		});
		DEFAULT_MAPPING_BY_CODE.put("f11", new RemoteKeyboardInstruction[] {
		    press(VK_F11), release(VK_F11)
		});
		DEFAULT_MAPPING_BY_CODE.put("f12", new RemoteKeyboardInstruction[] {
		    press(VK_F12), release(VK_F12)
		});
		DEFAULT_MAPPING_BY_CODE.put("f13", new RemoteKeyboardInstruction[] {
		    press(VK_F13), release(VK_F13)
		});
		DEFAULT_MAPPING_BY_CODE.put("f14", new RemoteKeyboardInstruction[] {
		    press(VK_F14), release(VK_F14)
		});
		DEFAULT_MAPPING_BY_CODE.put("f15", new RemoteKeyboardInstruction[] {
		    press(VK_F15), release(VK_F15)
		});
		DEFAULT_MAPPING_BY_CODE.put("f16", new RemoteKeyboardInstruction[] {
		    press(VK_F16), release(VK_F16)
		});
		DEFAULT_MAPPING_BY_CODE.put("f17", new RemoteKeyboardInstruction[] {
		    press(VK_F17), release(VK_F17)
		});
		DEFAULT_MAPPING_BY_CODE.put("f18", new RemoteKeyboardInstruction[] {
		    press(VK_F18), release(VK_F18)
		});
		DEFAULT_MAPPING_BY_CODE.put("f19", new RemoteKeyboardInstruction[] {
		    press(VK_F19), release(VK_F19)
		});
		DEFAULT_MAPPING_BY_CODE.put("f20", new RemoteKeyboardInstruction[] {
		    press(VK_F20), release(VK_F20)
		});
		DEFAULT_MAPPING_BY_CODE.put("f21", new RemoteKeyboardInstruction[] {
		    press(VK_F21), release(VK_F21)
		});
		DEFAULT_MAPPING_BY_CODE.put("f22", new RemoteKeyboardInstruction[] {
		    press(VK_F22), release(VK_F22)
		});
		DEFAULT_MAPPING_BY_CODE.put("f23", new RemoteKeyboardInstruction[] {
		    press(VK_F23), release(VK_F23)
		});
		DEFAULT_MAPPING_BY_CODE.put("f24", new RemoteKeyboardInstruction[] {
		    press(VK_F24), release(VK_F24)
		});

		DEFAULT_MAPPING_BY_CODE.put("print screen", new RemoteKeyboardInstruction[] {
		    press(VK_PRINTSCREEN), release(VK_PRINTSCREEN)
		});
		DEFAULT_MAPPING_BY_CODE.put("insert", new RemoteKeyboardInstruction[] {
		    press(VK_INSERT), release(VK_INSERT)
		});
		DEFAULT_MAPPING_BY_CODE.put("help", new RemoteKeyboardInstruction[] {
		    press(VK_HELP), release(VK_HELP)
		});
		DEFAULT_MAPPING_BY_CODE.put("back quote", new RemoteKeyboardInstruction[] {
		    press(VK_BACK_QUOTE), release(VK_BACK_QUOTE)
		});
		DEFAULT_MAPPING_BY_CODE.put("quote", new RemoteKeyboardInstruction[] {
		    press(VK_QUOTE), release(VK_QUOTE)
		});

		DEFAULT_MAPPING_BY_CODE.put("up", new RemoteKeyboardInstruction[] {
		    press(VK_KP_UP), release(VK_KP_UP)
		});
		DEFAULT_MAPPING_BY_CODE.put("down", new RemoteKeyboardInstruction[] {
		    press(VK_KP_DOWN), release(VK_KP_DOWN)
		});
		DEFAULT_MAPPING_BY_CODE.put("left", new RemoteKeyboardInstruction[] {
		    press(VK_KP_LEFT), release(VK_KP_LEFT)
		});
		DEFAULT_MAPPING_BY_CODE.put("right", new RemoteKeyboardInstruction[] {
		    press(VK_KP_RIGHT), release(VK_KP_RIGHT)
		});

		DEFAULT_MAPPING_BY_CODE.put("dead grave", new RemoteKeyboardInstruction[] {
		    press(VK_DEAD_GRAVE), release(VK_DEAD_GRAVE)
		});
		DEFAULT_MAPPING_BY_CODE.put("dead acute", new RemoteKeyboardInstruction[] {
		    press(VK_DEAD_ACUTE), release(VK_DEAD_ACUTE)
		});
		DEFAULT_MAPPING_BY_CODE.put("dead circumflex", new RemoteKeyboardInstruction[] {
		    press(VK_DEAD_CIRCUMFLEX), release(VK_DEAD_CIRCUMFLEX)
		});
		DEFAULT_MAPPING_BY_CODE.put("dead tilde", new RemoteKeyboardInstruction[] {
		    press(VK_DEAD_TILDE), release(VK_DEAD_TILDE)
		});
		DEFAULT_MAPPING_BY_CODE.put("dead macron", new RemoteKeyboardInstruction[] {
		    press(VK_DEAD_MACRON), release(VK_DEAD_MACRON)
		});
		DEFAULT_MAPPING_BY_CODE.put("dead breve", new RemoteKeyboardInstruction[] {
		    press(VK_DEAD_BREVE), release(VK_DEAD_BREVE)
		});
		DEFAULT_MAPPING_BY_CODE.put("dead above dot", new RemoteKeyboardInstruction[] {
		    press(VK_DEAD_ABOVEDOT), release(VK_DEAD_ABOVEDOT)
		});
		DEFAULT_MAPPING_BY_CODE.put("dead diaeresis", new RemoteKeyboardInstruction[] {
		    press(VK_DEAD_DIAERESIS), release(VK_DEAD_DIAERESIS)
		});
		DEFAULT_MAPPING_BY_CODE.put("dead above ring", new RemoteKeyboardInstruction[] {
		    press(VK_DEAD_ABOVERING), release(VK_DEAD_ABOVERING)
		});
		DEFAULT_MAPPING_BY_CODE.put("dead double acute", new RemoteKeyboardInstruction[] {
		    press(VK_DEAD_DOUBLEACUTE), release(VK_DEAD_DOUBLEACUTE)
		});
		DEFAULT_MAPPING_BY_CODE.put("dead caron", new RemoteKeyboardInstruction[] {
		    press(VK_DEAD_CARON), release(VK_DEAD_CARON)
		});
		DEFAULT_MAPPING_BY_CODE.put("dead cedilla", new RemoteKeyboardInstruction[] {
		    press(VK_DEAD_CEDILLA), release(VK_DEAD_CEDILLA)
		});
		DEFAULT_MAPPING_BY_CODE.put("dead ogonek", new RemoteKeyboardInstruction[] {
		    press(VK_DEAD_OGONEK), release(VK_DEAD_OGONEK)
		});
		DEFAULT_MAPPING_BY_CODE.put("dead iota", new RemoteKeyboardInstruction[] {
		    press(VK_DEAD_IOTA), release(VK_DEAD_IOTA)
		});
		DEFAULT_MAPPING_BY_CODE.put("dead voiced sound", new RemoteKeyboardInstruction[] {
		    press(VK_DEAD_VOICED_SOUND), release(VK_DEAD_VOICED_SOUND)
		});
		DEFAULT_MAPPING_BY_CODE.put("dead semivoiced sound", new RemoteKeyboardInstruction[] {
		    press(VK_DEAD_SEMIVOICED_SOUND), release(VK_DEAD_SEMIVOICED_SOUND)
		});

		DEFAULT_MAPPING_BY_CODE.put("ampersand", new RemoteKeyboardInstruction[] {
		    press(VK_AMPERSAND), release(VK_AMPERSAND)
		});
		DEFAULT_MAPPING_BY_CODE.put("asterisk", new RemoteKeyboardInstruction[] {
		    press(VK_ASTERISK), release(VK_ASTERISK)
		});
		DEFAULT_MAPPING_BY_CODE.put("double quote", new RemoteKeyboardInstruction[] {
		    press(VK_QUOTEDBL), release(VK_QUOTEDBL)
		});
		DEFAULT_MAPPING_BY_CODE.put("less", new RemoteKeyboardInstruction[] {
		    press(VK_LESS), release(VK_LESS)
		});
		DEFAULT_MAPPING_BY_CODE.put("greater", new RemoteKeyboardInstruction[] {
		    press(VK_GREATER), release(VK_GREATER)
		});
		DEFAULT_MAPPING_BY_CODE.put("left brace", new RemoteKeyboardInstruction[] {
		    press(VK_BRACELEFT), release(VK_BRACELEFT)
		});
		DEFAULT_MAPPING_BY_CODE.put("{", new RemoteKeyboardInstruction[] {
		    press(VK_BRACELEFT), release(VK_BRACELEFT)
		});
		DEFAULT_MAPPING_BY_CODE.put("right brace", new RemoteKeyboardInstruction[] {
		    press(VK_BRACERIGHT), release(VK_BRACERIGHT)
		});
		DEFAULT_MAPPING_BY_CODE.put("}", new RemoteKeyboardInstruction[] {
		    press(VK_BRACERIGHT), release(VK_BRACERIGHT)
		});
		DEFAULT_MAPPING_BY_CODE.put("at", new RemoteKeyboardInstruction[] {
		    press(VK_AT), release(VK_AT)
		});
		DEFAULT_MAPPING_BY_CODE.put("colon", new RemoteKeyboardInstruction[] {
		    press(VK_COLON), release(VK_COLON)
		});
		DEFAULT_MAPPING_BY_CODE.put("circumflex", new RemoteKeyboardInstruction[] {
		    press(VK_CIRCUMFLEX), release(VK_CIRCUMFLEX)
		});
		DEFAULT_MAPPING_BY_CODE.put("dollar", new RemoteKeyboardInstruction[] {
		    press(VK_DOLLAR), release(VK_DOLLAR)
		});
		DEFAULT_MAPPING_BY_CODE.put("euro", new RemoteKeyboardInstruction[] {
		    press(VK_EURO_SIGN), release(VK_EURO_SIGN)
		});
		DEFAULT_MAPPING_BY_CODE.put("exclamation mark", new RemoteKeyboardInstruction[] {
		    press(VK_EXCLAMATION_MARK), release(VK_EXCLAMATION_MARK)
		});
		DEFAULT_MAPPING_BY_CODE.put("inverted exclamation mark", new RemoteKeyboardInstruction[] {
		    press(VK_INVERTED_EXCLAMATION_MARK), release(VK_INVERTED_EXCLAMATION_MARK)
		});
		DEFAULT_MAPPING_BY_CODE.put("left parenthesis", new RemoteKeyboardInstruction[] {
		    press(VK_LEFT_PARENTHESIS), release(VK_LEFT_PARENTHESIS)
		});
		DEFAULT_MAPPING_BY_CODE.put("number sign", new RemoteKeyboardInstruction[] {
		    press(VK_NUMBER_SIGN), release(VK_NUMBER_SIGN)
		});
		DEFAULT_MAPPING_BY_CODE.put("minus", new RemoteKeyboardInstruction[] {
		    press(VK_MINUS), release(VK_MINUS)
		});
		DEFAULT_MAPPING_BY_CODE.put("plus", new RemoteKeyboardInstruction[] {
		    press(VK_PLUS), release(VK_PLUS)
		});
		DEFAULT_MAPPING_BY_CODE.put("right parenthesis", new RemoteKeyboardInstruction[] {
		    press(VK_RIGHT_PARENTHESIS), release(VK_RIGHT_PARENTHESIS)
		});
		DEFAULT_MAPPING_BY_CODE.put("underscore", new RemoteKeyboardInstruction[] {
		    press(VK_UNDERSCORE), release(VK_UNDERSCORE)
		});

		DEFAULT_MAPPING_BY_CODE.put("final", new RemoteKeyboardInstruction[] {
		    press(VK_FINAL), release(VK_FINAL)
		});
		DEFAULT_MAPPING_BY_CODE.put("convert", new RemoteKeyboardInstruction[] {
		    press(VK_CONVERT), release(VK_CONVERT)
		});
		DEFAULT_MAPPING_BY_CODE.put("no convert", new RemoteKeyboardInstruction[] {
		    press(VK_NONCONVERT), release(VK_NONCONVERT)
		});
		DEFAULT_MAPPING_BY_CODE.put("accept", new RemoteKeyboardInstruction[] {
		    press(VK_ACCEPT), release(VK_ACCEPT)
		});
		DEFAULT_MAPPING_BY_CODE.put("mode change", new RemoteKeyboardInstruction[] {
		    press(VK_MODECHANGE), release(VK_MODECHANGE)
		});
		DEFAULT_MAPPING_BY_CODE.put("kana", new RemoteKeyboardInstruction[] {
		    press(VK_KANA), release(VK_KANA)
		});
		DEFAULT_MAPPING_BY_CODE.put("kanji", new RemoteKeyboardInstruction[] {
		    press(VK_KANJI), release(VK_KANJI)
		});
		DEFAULT_MAPPING_BY_CODE.put("alphanumeric", new RemoteKeyboardInstruction[] {
		    press(VK_ALPHANUMERIC), release(VK_ALPHANUMERIC)
		});
		DEFAULT_MAPPING_BY_CODE.put("katakana", new RemoteKeyboardInstruction[] {
		    press(VK_KATAKANA), release(VK_KATAKANA)
		});
		DEFAULT_MAPPING_BY_CODE.put("hiragana", new RemoteKeyboardInstruction[] {
		    press(VK_HIRAGANA), release(VK_HIRAGANA)
		});
		DEFAULT_MAPPING_BY_CODE.put("full-width", new RemoteKeyboardInstruction[] {
		    press(VK_FULL_WIDTH), release(VK_FULL_WIDTH)
		});
		DEFAULT_MAPPING_BY_CODE.put("half-width", new RemoteKeyboardInstruction[] {
		    press(VK_HALF_WIDTH), release(VK_HALF_WIDTH)
		});
		DEFAULT_MAPPING_BY_CODE.put("roman characters", new RemoteKeyboardInstruction[] {
		    press(VK_ROMAN_CHARACTERS), release(VK_ROMAN_CHARACTERS)
		});
		DEFAULT_MAPPING_BY_CODE.put("all candidates", new RemoteKeyboardInstruction[] {
		    press(VK_ALL_CANDIDATES), release(VK_ALL_CANDIDATES)
		});
		DEFAULT_MAPPING_BY_CODE.put("previous candidate", new RemoteKeyboardInstruction[] {
		    press(VK_PREVIOUS_CANDIDATE), release(VK_PREVIOUS_CANDIDATE)
		});
		DEFAULT_MAPPING_BY_CODE.put("code input", new RemoteKeyboardInstruction[] {
		    press(VK_CODE_INPUT), release(VK_CODE_INPUT)
		});
		DEFAULT_MAPPING_BY_CODE.put("japanese katakana", new RemoteKeyboardInstruction[] {
		    press(VK_JAPANESE_KATAKANA), release(VK_JAPANESE_KATAKANA)
		});
		DEFAULT_MAPPING_BY_CODE.put("japanese hiragana", new RemoteKeyboardInstruction[] {
		    press(VK_JAPANESE_HIRAGANA), release(VK_JAPANESE_HIRAGANA)
		});
		DEFAULT_MAPPING_BY_CODE.put("japanese roman", new RemoteKeyboardInstruction[] {
		    press(VK_JAPANESE_ROMAN), release(VK_JAPANESE_ROMAN)
		});
		DEFAULT_MAPPING_BY_CODE.put("kana lock", new RemoteKeyboardInstruction[] {
		    press(VK_KANA_LOCK), release(VK_KANA_LOCK)
		});
		DEFAULT_MAPPING_BY_CODE.put("input method on/off", new RemoteKeyboardInstruction[] {
		    press(VK_INPUT_METHOD_ON_OFF), release(VK_INPUT_METHOD_ON_OFF)
		});

		DEFAULT_MAPPING_BY_CODE.put("again", new RemoteKeyboardInstruction[] {
		    press(VK_AGAIN), release(VK_AGAIN)
		});
		DEFAULT_MAPPING_BY_CODE.put("undo", new RemoteKeyboardInstruction[] {
		    press(VK_UNDO), release(VK_UNDO)
		});
		DEFAULT_MAPPING_BY_CODE.put("copy", new RemoteKeyboardInstruction[] {
		    press(VK_COPY), release(VK_COPY)
		});
		DEFAULT_MAPPING_BY_CODE.put("paste", new RemoteKeyboardInstruction[] {
		    press(VK_PASTE), release(VK_PASTE)
		});
		DEFAULT_MAPPING_BY_CODE.put("cut", new RemoteKeyboardInstruction[] {
		    press(VK_CUT), release(VK_CUT)
		});
		DEFAULT_MAPPING_BY_CODE.put("find", new RemoteKeyboardInstruction[] {
		    press(VK_FIND), release(VK_FIND)
		});
		DEFAULT_MAPPING_BY_CODE.put("props", new RemoteKeyboardInstruction[] {
		    press(VK_PROPS), release(VK_PROPS)
		});
		DEFAULT_MAPPING_BY_CODE.put("stop", new RemoteKeyboardInstruction[] {
		    press(VK_STOP), release(VK_STOP)
		});

		//		for (char ch = 0; ch < 256; ch += 1)
		//		{
		//			List<RemoteKeyboardInstruction> instructions = new ArrayList<KeyMapping.RemoteKeyboardInstruction>();
		//			String number = String.valueOf((int) ch);
		//
		//			instructions.add(press(VK_ALT));
		//
		//			for (int i = 0; i < number.length(); i += 1)
		//			{
		//				instructions.add(press(VK_NUMPAD0 + (number.charAt(i) - '0')));
		//				instructions.add(release(VK_NUMPAD0 + (number.charAt(i) - '0')));
		//			}
		//
		//			instructions.add(release(VK_ALT));
		//			
		//			DEFAULT_MAPPING_BY_KEY.put(ch, instructions.toArray(new RemoteKeyboardInstruction[instructions.size()]));
		//		}

		for (char ch = '0'; ch <= '9'; ch += 1)
		{
			DEFAULT_MAPPING_BY_KEY.put(ch, new RemoteKeyboardInstruction[] {
			    press(ch), release(ch)
			});
		}

		for (char ch = 'A'; ch <= 'Z'; ch += 1)
		{
			DEFAULT_MAPPING_BY_KEY.put(Character.toLowerCase(ch), new RemoteKeyboardInstruction[] {
			    press(ch), release(ch)
			});
			DEFAULT_MAPPING_BY_KEY.put(ch, new RemoteKeyboardInstruction[] {
			    press(VK_SHIFT), press(ch), release(ch), release(VK_SHIFT)
			});
		}

		DEFAULT_MAPPING_BY_KEY.put('\b', new RemoteKeyboardInstruction[] {
		    press(VK_BACK_SPACE), release(VK_BACK_SPACE)
		});
		DEFAULT_MAPPING_BY_KEY.put('\t', new RemoteKeyboardInstruction[] {
		    press(VK_TAB), release(VK_TAB)
		});
		DEFAULT_MAPPING_BY_KEY.put(' ', new RemoteKeyboardInstruction[] {
		    press(VK_SPACE), release(VK_SPACE)
		});

		DEFAULT_MAPPING_BY_KEY.put(',', new RemoteKeyboardInstruction[] {
		    press(VK_COMMA), release(VK_COMMA)
		});
		DEFAULT_MAPPING_BY_KEY.put('.', new RemoteKeyboardInstruction[] {
		    press(VK_PERIOD), release(VK_PERIOD)
		});
		DEFAULT_MAPPING_BY_KEY.put('/', new RemoteKeyboardInstruction[] {
		    press(VK_SLASH), release(VK_SLASH)
		});
		DEFAULT_MAPPING_BY_KEY.put(';', new RemoteKeyboardInstruction[] {
		    press(VK_SEMICOLON), release(VK_SEMICOLON)
		});
		DEFAULT_MAPPING_BY_KEY.put('=', new RemoteKeyboardInstruction[] {
		    press(VK_EQUALS), release(VK_EQUALS)
		});
		DEFAULT_MAPPING_BY_KEY.put('[', new RemoteKeyboardInstruction[] {
		    press(VK_OPEN_BRACKET), release(VK_OPEN_BRACKET)
		});
		DEFAULT_MAPPING_BY_KEY.put('\\', new RemoteKeyboardInstruction[] {
		    press(VK_BACK_SLASH), release(VK_BACK_SLASH)
		});
		DEFAULT_MAPPING_BY_KEY.put(']', new RemoteKeyboardInstruction[] {
		    press(VK_CLOSE_BRACKET), release(VK_CLOSE_BRACKET)
		});

		DEFAULT_MAPPING_BY_KEY.put('`', new RemoteKeyboardInstruction[] {
		    press(VK_BACK_QUOTE), release(VK_BACK_QUOTE)
		});
		DEFAULT_MAPPING_BY_KEY.put('\'', new RemoteKeyboardInstruction[] {
		    press(VK_QUOTE), release(VK_QUOTE)
		});

		DEFAULT_MAPPING_BY_KEY.put('&', new RemoteKeyboardInstruction[] {
		    press(VK_AMPERSAND), release(VK_AMPERSAND)
		});
		DEFAULT_MAPPING_BY_KEY.put('*', new RemoteKeyboardInstruction[] {
		    press(VK_ASTERISK), release(VK_ASTERISK)
		});
		DEFAULT_MAPPING_BY_KEY.put('\"', new RemoteKeyboardInstruction[] {
		    press(VK_QUOTEDBL), release(VK_QUOTEDBL)
		});
		DEFAULT_MAPPING_BY_KEY.put('<', new RemoteKeyboardInstruction[] {
		    press(VK_LESS), release(VK_LESS)
		});
		DEFAULT_MAPPING_BY_KEY.put('>', new RemoteKeyboardInstruction[] {
		    press(VK_GREATER), release(VK_GREATER)
		});
		DEFAULT_MAPPING_BY_KEY.put('{', new RemoteKeyboardInstruction[] {
		    press(VK_BRACELEFT), release(VK_BRACELEFT)
		});
		DEFAULT_MAPPING_BY_KEY.put('}', new RemoteKeyboardInstruction[] {
		    press(VK_BRACERIGHT), release(VK_BRACERIGHT)
		});
		DEFAULT_MAPPING_BY_KEY.put('@', new RemoteKeyboardInstruction[] {
		    press(VK_AT), release(VK_AT)
		});
		DEFAULT_MAPPING_BY_KEY.put(',', new RemoteKeyboardInstruction[] {
		    press(VK_COLON), release(VK_COLON)
		});
		DEFAULT_MAPPING_BY_KEY.put('^', new RemoteKeyboardInstruction[] {
		    press(VK_CIRCUMFLEX), release(VK_CIRCUMFLEX)
		});
		DEFAULT_MAPPING_BY_KEY.put('$', new RemoteKeyboardInstruction[] {
		    press(VK_DOLLAR), release(VK_DOLLAR)
		});
		DEFAULT_MAPPING_BY_KEY.put('\u20ac', new RemoteKeyboardInstruction[] {
		    press(VK_EURO_SIGN), release(VK_EURO_SIGN)
		});
		DEFAULT_MAPPING_BY_KEY.put('!', new RemoteKeyboardInstruction[] {
		    press(VK_EXCLAMATION_MARK), release(VK_EXCLAMATION_MARK)
		});
		DEFAULT_MAPPING_BY_KEY.put('\u00a1', new RemoteKeyboardInstruction[] {
		    press(VK_INVERTED_EXCLAMATION_MARK), release(VK_INVERTED_EXCLAMATION_MARK)
		});
		DEFAULT_MAPPING_BY_KEY.put('(', new RemoteKeyboardInstruction[] {
		    press(VK_LEFT_PARENTHESIS), release(VK_LEFT_PARENTHESIS)
		});
		DEFAULT_MAPPING_BY_KEY.put('#', new RemoteKeyboardInstruction[] {
		    press(VK_NUMBER_SIGN), release(VK_NUMBER_SIGN)
		});
		DEFAULT_MAPPING_BY_KEY.put('-', new RemoteKeyboardInstruction[] {
		    press(VK_MINUS), release(VK_MINUS)
		});
		DEFAULT_MAPPING_BY_KEY.put('+', new RemoteKeyboardInstruction[] {
		    press(VK_PLUS), release(VK_PLUS)
		});
		DEFAULT_MAPPING_BY_KEY.put(')', new RemoteKeyboardInstruction[] {
		    press(VK_RIGHT_PARENTHESIS), release(VK_RIGHT_PARENTHESIS)
		});
		DEFAULT_MAPPING_BY_KEY.put('_', new RemoteKeyboardInstruction[] {
		    press(VK_UNDERSCORE), release(VK_UNDERSCORE)
		});
	}

	private final Map<String, RemoteKeyboardInstruction[]> mappingByCode;
	private final Map<Character, RemoteKeyboardInstruction[]> mappingByCharacter;

	protected RemoteKeyboard()
	{
		super();

		mappingByCode = new HashMap<String, RemoteKeyboardInstruction[]>();
		mappingByCharacter = new HashMap<Character, RemoteKeyboardInstruction[]>();
	}

	/**
	 * Registers the specified instructions for the specified key code
	 * 
	 * @param code the code
	 * @param instructions the instructions
	 */
	public void register(String code, RemoteKeyboardInstruction... instructions)
	{
		mappingByCode.put(code, instructions);
	}

	/**
	 * Registers the specified instructions for the specified character
	 * 
	 * @param character the character
	 * @param instructions the instructions
	 */
	public void register(char character, RemoteKeyboardInstruction... instructions)
	{
		mappingByCharacter.put(character, instructions);
	}

	/**
	 * Parses the string according to the following specification:
	 * 
	 * <pre>
	 * string = { CHARACTER | "{" command "}" }.
	 * command = ( "pause " seconds ) | keyCommand.
	 * keyCommand = { "shift" | "control" | "ctrl" | "alt graph" | "alt gr" | "altgr" | "alt" | "meta" | "windows" | "win" } 
	 *              ( "key 0x" hexCode | CODE | CHARACTER ).
	 * </pre>
	 * 
	 * @param results the instruction list
	 * @param s the string, stating at pos 0
	 * @return the command
	 */
	public List<RemoteKeyboardInstruction> parse(String s)
	{
		return parse(new ArrayList<RemoteKeyboardInstruction>(), s);
	}

	private List<RemoteKeyboardInstruction> parse(List<RemoteKeyboardInstruction> results, String s)
	{
		int pos = 0;

		while (pos < s.length())
		{
			char ch = s.charAt(pos);

			if (ch == '{')
			{
				int endPos = s.indexOf('}', pos);

				if (endPos >= 0)
				{
					String substring = s.substring(pos + 1, endPos).toLowerCase(Locale.getDefault());

					if (parseCommand(results, substring))
					{
						pos = endPos + 1;
						continue;
					}
				}
			}

			parseCharacter(results, ch);
			pos += 1;
		}

		return results;
	}

	private boolean parseCommand(List<RemoteKeyboardInstruction> results, String s)
	{
		if (s.startsWith("pause "))
		{
			try
			{
				results.add(pause(Double.parseDouble(s.substring(6).trim())));

				return true;
			}
			catch (NumberFormatException e)
			{
				// failed to parse number
				return false;
			}
		}

		return parseKeyCommand(results, s);
	}

	private boolean parseKeyCommand(List<RemoteKeyboardInstruction> results, String s)
	{
		s = s.trim();
		int index = results.size();

		if (s.startsWith("shift "))
		{
			if (parseKeyCommand(results, s.substring(s.indexOf(' '))))
			{
				results.add(index, press(VK_SHIFT));
				results.add(release(VK_SHIFT));
				return true;
			}

			return false;
		}

		if ((s.startsWith("control ")) || (s.startsWith("ctrl ")))
		{
			if (parseKeyCommand(results, s.substring(s.indexOf(' '))))
			{
				results.add(index, press(VK_CONTROL));
				results.add(release(VK_CONTROL));
				return true;
			}

			return false;
		}

		if ((s.startsWith("alt graph ")) || (s.startsWith("alt gr ")) || (s.startsWith("altgr ")))
		{
			if (parseKeyCommand(results, s.substring(s.indexOf(' '))))
			{
				results.add(index, press(VK_ALT_GRAPH));
				results.add(release(VK_ALT_GRAPH));
				return true;
			}

			return false;
		}

		if (s.startsWith("alt "))
		{
			if (parseKeyCommand(results, s.substring(s.indexOf(' '))))
			{
				results.add(index, press(VK_ALT));
				results.add(release(VK_ALT));
				return true;
			}

			return false;
		}

		if (s.startsWith("meta "))
		{
			if (parseKeyCommand(results, s.substring(s.indexOf(' '))))
			{
				results.add(index, press(VK_META));
				results.add(release(VK_META));
				return true;
			}

			return false;
		}

		if ((s.startsWith("windows ")) || (s.startsWith("win ")))
		{
			if (parseKeyCommand(results, s.substring(s.indexOf(' '))))
			{
				results.add(index, press(VK_WINDOWS));
				results.add(release(VK_WINDOWS));
				return true;
			}

			return false;
		}

		if (s.startsWith("key 0x"))
		{
			try
			{
				int keyCode = Integer.parseInt(s.substring(6).trim(), 16);

				results.add(press(keyCode));
				results.add(release(keyCode));

				return true;
			}
			catch (NumberFormatException e)
			{
				return false;
			}
		}

		RemoteKeyboardInstruction[] instructions = mappingByCode.get(s);

		if (instructions == null)
		{
			instructions = DEFAULT_MAPPING_BY_CODE.get(s);
		}

		if ((instructions == null) && (s.length() == 1))
		{
			return parseCharacter(results, s.charAt(0));
		}

		if (instructions != null)
		{
			for (RemoteKeyboardInstruction instruction : instructions)
			{
				results.add(instruction);
			}

			return true;
		}

		return false;
	}

	private boolean parseCharacter(List<RemoteKeyboardInstruction> results, char ch)
	{
		RemoteKeyboardInstruction[] instructions = mappingByCharacter.get(ch);

		if (instructions == null)
		{
			instructions = DEFAULT_MAPPING_BY_KEY.get(ch);
		}

		if (instructions != null)
		{
			for (RemoteKeyboardInstruction instruction : instructions)
			{
				results.add(instruction);
			}

			return true;
		}

		// TODO error handling of untypeable character?
		return false;
	}

}
