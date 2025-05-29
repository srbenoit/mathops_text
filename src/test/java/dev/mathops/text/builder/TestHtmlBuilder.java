package dev.mathops.text.builder;

import dev.mathops.commons.CoreConstants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for the {@code HTMLBuilder} class.
 */
final class TestHtmlBuilder {

    /** A common string. */
    private static final String NAN = "NaN";

    /** A common string. */
    private static final String NAN_CRLF = "NaN\r\n";

    /** A common string. */
    private static final String HTML = "html";

    /** A common string. */
    private static final String DIV_TAG = "<div>";

    /** A common string. */
    private static final String ABC = "abc";

    /** A common string. */
    private static final String XYZ = "xyz";

    /** A common string. */
    private static final String XYZ_CRLF = "xyz\r\n";

    /** A common string. */
    private static final String INF = "Infinity";

    /** A common string. */
    private static final String INF_CRLF = "Infinity\r\n";

    /** A common string. */
    private static final String NEGINF = "-Infinity";

    /** A common string. */
    private static final String NEGINF_CRLF = "-Infinity\r\n";

    /** A common string. */
    private static final String CHAR_MISMATCH = "Character mismatch";

    /** A common string. */
    private static final String TOSTR_MISMATCH = "ToString mismatch";

    /** A common string. */
    private static final String EXPECT_LEN_3 = "Expected length to be 3";

    /** A common string. */
    private static final String EXPECT_LEN_4 = "Expected length to be 4";

    /** A common string. */
    private static final String EXPECT_LEN_5 = "Expected length to be 5";

    /** A common string. */
    private static final String EXPECT_LEN_6 = "Expected length to be 6";

    /** A common string. */
    private static final String EXPECT_LEN_7 = "Expected length to be 7";

    /** A common string. */
    private static final String ATTR_A_EQ_B = "a='b'";

    /** A common string. */
    private static final String ATTR_C_EQ_D = "c='d'";

    /**
     * A test case.
     */
    @Test
    @DisplayName("Length after add(char)")
    void test001() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.add('x');

        final int length = htm.length();
        assertEquals(1, length, "Expected length to be 1");
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("Character 0 after add(char)")
    void test002() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.add('x');

        final char actual = htm.charAt(0);
        assertEquals('x', actual, CHAR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after add(char)")
    void test003() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.add('x');

        final String actual = htm.toString();
        assertEquals("x", actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("Length value after add(char[])")
    void test004() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.add(new char[]{'x', 'y', 'z'});

        final int length = htm.length();
        assertEquals(3, length, EXPECT_LEN_3);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("Characters after add(char[])")
    void test005() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.add(new char[]{'x', 'y', 'z'});

        final char ch0 = htm.charAt(0);
        assertEquals('x', ch0, CHAR_MISMATCH);
        final char ch1 = htm.charAt(1);
        assertEquals('y', ch1, CHAR_MISMATCH);
        final char ch2 = htm.charAt(2);
        assertEquals('z', ch2, CHAR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after add(char[])")
    void test006() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.add(new char[]{'x', 'y', 'z'});

        final String actual = htm.toString();
        assertEquals(XYZ, actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("Length after add(String)")
    void test007() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.add(XYZ);

        final int length = htm.length();
        assertEquals(3, length, EXPECT_LEN_3);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("Characters after add(String)")
    void test008() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.add(XYZ);

        final char ch0 = htm.charAt(0);
        assertEquals('x', ch0, CHAR_MISMATCH);
        final char ch1 = htm.charAt(1);
        assertEquals('y', ch1, CHAR_MISMATCH);
        final char ch2 = htm.charAt(2);
        assertEquals('z', ch2, CHAR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after add(String)")
    void test009() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.add(XYZ);

        final String actual = htm.toString();
        assertEquals(XYZ, actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("Length after add(true)")
    void test010() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.add(true);

        final int length = htm.length();
        assertEquals(4, length, EXPECT_LEN_4);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after add(true)")
    void test011() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.add(true);

        final String actual = htm.toString();
        assertEquals("true", actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("Length after add(false)")
    void test012() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.add(false);

        final int length = htm.length();
        assertEquals(5, length, EXPECT_LEN_5);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after add(false)")
    void test013() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.add(false);

        final String actual = htm.toString();
        assertEquals("false", actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("Length after add(int)")
    void test014() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.add(-123);

        final int length = htm.length();
        assertEquals(4, length, EXPECT_LEN_4);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after add(int)")
    void test015() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.add(-123);

        final String actual = htm.toString();
        assertEquals("-123", actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("Length after add(long)")
    void test016() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.add(-1234567890123L);

        final int length = htm.length();
        assertEquals(14, length, "Expected length to be 14");
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after add(long)")
    void test017() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.add(-1234567890123L);

        final String actual = htm.toString();
        assertEquals("-1234567890123", actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("Length after add(float)")
    void test018() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.add(1.234f);

        final int length = htm.length();
        assertEquals(5, length, EXPECT_LEN_5);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after add(float)")
    void test019() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.add(1.234f);

        final String actual = htm.toString();
        assertEquals("1.234", actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("Length after add(float NaN)")
    void test020() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.add(Float.NaN);

        final int length = htm.length();
        assertEquals(3, length, EXPECT_LEN_3);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after add(float NaN)")
    void test021() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.add(Float.NaN);

        final String actual = htm.toString();
        assertEquals(NAN, actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after add(float POSINF)")
    void test022() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.add(Float.POSITIVE_INFINITY);

        final String actual = htm.toString();
        assertEquals(INF, actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after add(float NEGINF)")
    void test023() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.add(Float.NEGATIVE_INFINITY);

        final String actual = htm.toString();
        assertEquals(NEGINF, actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("Length value after add(double)")
    void test024() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.add(1.234564789);

        final int length = htm.length();
        assertEquals(11, length, "Expected length to be 11");
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after add(double)")
    void test025() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.add(1.23456789);

        final String actual = htm.toString();
        assertEquals("1.23456789", actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("Length after add(double NaN)")
    void test026() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.add(Double.NaN);

        final int length = htm.length();
        assertEquals(3, length, EXPECT_LEN_3);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after add(double NaN)")
    void test027() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.add(Double.NaN);

        final String actual = htm.toString();
        assertEquals(NAN, actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after add(double POSINF)")
    void test028() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.add(Double.POSITIVE_INFINITY);

        final String actual = htm.toString();
        assertEquals(INF, actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after add(double NEGINF)")
    void test029() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.add(Double.NEGATIVE_INFINITY);

        final String actual = htm.toString();
        assertEquals(NEGINF, actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after add(LocalDate)")
    void test030() {

        final LocalDate ld = LocalDate.now();
        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.add(ld);

        final String actual = htm.toString();
        final String expect = ld.toString();
        assertEquals(expect, actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after add((Object)null)")
    void test031() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.add((Object) null);

        final String actual = htm.toString();
        assertEquals("null", actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after add((Object[])null)")
    void test032() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.add((Object[]) null);

        final String actual = htm.toString();
        assertEquals(CoreConstants.EMPTY, actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after add(LocalDate, LocalTime)")
    void test033() {

        final LocalDate ld = LocalDate.now();
        final LocalTime lt = LocalTime.now();
        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.add(ld, lt);

        final String expectTime = lt.toString();
        final String actual = htm.toString();
        assertEquals(ld + expectTime, actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after add(Object[]{LocalDate, LocalTime})")
    void test034() {

        final LocalDate ld = LocalDate.now();
        final LocalTime lt = LocalTime.now();
        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.add(ld, lt);

        final String expectTime = lt.toString();
        final String actual = htm.toString();
        assertEquals(ld + expectTime, actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("Length after addln")
    void test035() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.addln();

        final int length = htm.length();
        assertEquals(2, length, "Length mismatch after addln");
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after addln")
    void test036() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.addln();

        final String actual = htm.toString();
        assertEquals(CoreConstants.CRLF, actual, "String mismatch after addln");
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("Length after addln(char)")
    void test037() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.addln('x');

        final int length = htm.length();
        assertEquals(3, length, EXPECT_LEN_3);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("Characters after addln(char)")
    void test038() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.addln('x');

        final char ch0 = htm.charAt(0);
        assertEquals('x', ch0, CHAR_MISMATCH);
        final char ch1 = htm.charAt(1);
        assertEquals('\r', ch1, CHAR_MISMATCH);
        final char ch2 = htm.charAt(2);
        assertEquals('\n', ch2, CHAR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after addln(char)")
    void test039() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.addln('x');

        final String actual = htm.toString();
        assertEquals("x\r\n", actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("Length after addln(char[])")
    void test040() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        final char[] chars = {'x', 'y', 'z'};
        htm.addln(chars);

        final int length = htm.length();
        assertEquals(5, length, EXPECT_LEN_5);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("Characters after addln(char[])")
    void test041() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        final char[] chars = {'x', 'y', 'z'};
        htm.addln(chars);

        final char ch0 = htm.charAt(0);
        assertEquals('x', ch0, CHAR_MISMATCH);
        final char ch1 = htm.charAt(1);
        assertEquals('y', ch1, CHAR_MISMATCH);
        final char ch2 = htm.charAt(2);
        assertEquals('z', ch2, CHAR_MISMATCH);
        final char ch3 = htm.charAt(3);
        assertEquals('\r', ch3, CHAR_MISMATCH);
        final char ch4 = htm.charAt(4);
        assertEquals('\n', ch4, CHAR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after addln(char[])")
    void test042() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        final char[] chars = {'x', 'y', 'z'};
        htm.addln(chars);

        final String actual = htm.toString();
        assertEquals(XYZ_CRLF, actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("Length after addln(String)")
    void test043() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.addln(XYZ);

        final int length = htm.length();
        assertEquals(5, length, EXPECT_LEN_3);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("Characters after addln(String)")
    void test044() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.addln(XYZ);

        final char ch0 = htm.charAt(0);
        assertEquals('x', ch0, CHAR_MISMATCH);
        final char ch1 = htm.charAt(1);
        assertEquals('y', ch1, CHAR_MISMATCH);
        final char ch2 = htm.charAt(2);
        assertEquals('z', ch2, CHAR_MISMATCH);
        final char ch3 = htm.charAt(3);
        assertEquals('\r', ch3, CHAR_MISMATCH);
        final char ch4 = htm.charAt(4);
        assertEquals('\n', ch4, CHAR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after addln(String)")
    void test045() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.addln(XYZ);

        final String actual = htm.toString();
        assertEquals(XYZ_CRLF, actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("Length after addln(true)")
    void test046() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.addln(true);

        final int length = htm.length();
        assertEquals(6, length, EXPECT_LEN_6);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after addln(true)")
    void test047() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.addln(true);

        final String actual = htm.toString();
        assertEquals("true\r\n", actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("Length after addln(false)")
    void test048() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.addln(false);

        final int length = htm.length();
        assertEquals(7, length, EXPECT_LEN_7);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after addln(false)")
    void test049() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.addln(false);

        final String actual = htm.toString();
        assertEquals("false\r\n", actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("Length after addln(int)")
    void test050() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.addln(-123);

        final int length = htm.length();
        assertEquals(6, length, EXPECT_LEN_6);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after addln(int)")
    void test051() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.addln(-123);

        final String actual = htm.toString();
        assertEquals("-123\r\n", actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("Length after addln(long)")
    void test052() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.addln(-1234567890123L);

        final int length = htm.length();
        assertEquals(16, length, "Expected length to be 16");
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after addln(long)")
    void test053() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.addln(-1234567890123L);

        final String actual = htm.toString();
        assertEquals("-1234567890123\r\n", actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("Length after addln(float)")
    void test054() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.addln(1.234f);

        final int length = htm.length();
        assertEquals(7, length, EXPECT_LEN_7);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after addln(float)")
    void test055() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.addln(1.234f);

        final String actual = htm.toString();
        assertEquals("1.234\r\n", actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("Length after addln(float NaN)")
    void test056() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.addln(Float.NaN);

        final int length = htm.length();
        assertEquals(5, length, EXPECT_LEN_5);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after addln(float NaN)")
    void test057() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.addln(Float.NaN);

        final String actual = htm.toString();
        assertEquals(NAN_CRLF, actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after addln(float POSINF)")
    void test058() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.addln(Float.POSITIVE_INFINITY);

        final String actual = htm.toString();
        assertEquals(INF_CRLF, actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after addln(float NEGINF)")
    void test059() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.addln(Float.NEGATIVE_INFINITY);

        final String actual = htm.toString();
        assertEquals(NEGINF_CRLF, actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("Length after addln(double)")
    void test060() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.addln(1.234564789);

        final int length = htm.length();
        assertEquals(13, length, "Expected length to be 13");
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after addln(double)")
    void test061() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.addln(1.23456789);

        final String actual = htm.toString();
        assertEquals("1.23456789\r\n", actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("Length after addln(double NaN)")
    void test062() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.addln(Double.NaN);

        final int length = htm.length();
        assertEquals(5, length, EXPECT_LEN_5);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after addln(double NaN)")
    void test063() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.addln(Double.NaN);

        final String actual = htm.toString();
        assertEquals(NAN_CRLF, actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after addln(double POSINF)")
    void test064() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.addln(Double.POSITIVE_INFINITY);

        final String actual = htm.toString();
        assertEquals(INF_CRLF, actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after addln(double NEGINF)")
    void test065() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.addln(Double.NEGATIVE_INFINITY);

        final String actual = htm.toString();
        assertEquals(NEGINF_CRLF, actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after addln(LocalDate)")
    void test066() {

        final LocalDate ld = LocalDate.now();
        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.addln(ld);

        final String actual = htm.toString();
        assertEquals(ld + CoreConstants.CRLF, actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after addln((Object)null)")
    void test067() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.addln((Object) null);

        final String actual = htm.toString();
        assertEquals("null\r\n", actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after addln((Object[])null)")
    void test068() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.addln((Object[]) null);

        final String actual = htm.toString();
        assertEquals(CoreConstants.CRLF, actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after addln(LocalDate, LocalTime)")
    void test069() {

        final LocalDate ld = LocalDate.now();
        final LocalTime lt = LocalTime.now();
        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.addln(ld, lt);

        final String expectTime = lt.toString();
        final String actual = htm.toString();
        assertEquals(ld + expectTime + CoreConstants.CRLF, actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after addln(Object[]{LocalDate, LocalTime})")
    void test070() {

        final LocalDate ld = LocalDate.now();
        final LocalTime lt = LocalTime.now();
        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.addln(ld, lt);

        final String expectTime = lt.toString();
        final String actual = htm.toString();
        assertEquals(ld + expectTime + CoreConstants.CRLF, actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after indent(5)")
    void test071() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.indent(5);

        final String actual = htm.toString();
        assertEquals("     ", actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after indent(0)")
    void test072() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.indent(0);

        final String actual = htm.toString();
        assertEquals(CoreConstants.EMPTY, actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after indent(-10)")
    void test073() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.indent(-10);

        final String actual = htm.toString();
        assertEquals(CoreConstants.EMPTY, actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after addEscaped() with no escapes")
    void test074() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.addEscaped(XYZ);

        final String actual = htm.toString();
        assertEquals(XYZ, actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after addEscaped() with ampersand")
    void test075() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.addEscaped("this&that");

        final String actual = htm.toString();
        assertEquals("this&amp;that", actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after addEscaped() with apostrophe")
    void test076() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.addEscaped("foo='abc'");

        final String actual = htm.toString();
        assertEquals("foo=&apos;abc&apos;", actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after addEscaped() with quotes")
    void test077() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.addEscaped("foo=\"abc\"");

        final String actual = htm.toString();
        assertEquals("foo=&quot;abc&quot;", actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after addEscaped() with angle brackets")
    void test078() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.addEscaped("foo=<html>");

        final String actual = htm.toString();
        assertEquals("foo=&lt;html&gt;", actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after addAttribute")
    void test079() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.addAttribute(XYZ, ABC, 0);

        final String actual = htm.toString();
        assertEquals(" xyz='abc'", actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after addAttribute with indent")
    void test080() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.addAttribute(XYZ, ABC, 4);

        final String actual = htm.toString();
        assertEquals("\r\n    xyz='abc'", actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after addAttribute with escapes")
    void test081() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.addAttribute("expr", "0<x<10", 0);

        final String actual = htm.toString();
        assertEquals(" expr='0&lt;x&lt;10'", actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after openElement with indent")
    void test082() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.openElement(5, HTML);

        final String actual = htm.toString();
        assertEquals("     <html", actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after startNonempty with indent, no newline")
    void test083() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.startNonempty(5, HTML, false);

        final String actual = htm.toString();
        assertEquals("     <html>", actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after startNonempty with indent, newline")
    void test084() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.startNonempty(5, HTML, true);

        final String actual = htm.toString();
        assertEquals("     <html>\r\n", actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after endNonempty with indent, no newline")
    void test085() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.endNonempty(5, HTML, false);

        final String actual = htm.toString();
        assertEquals("     </html>", actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after endNonempty with indent, newline")
    void test086() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.endNonempty(5, HTML, true);

        final String actual = htm.toString();
        assertEquals("     </html>\r\n", actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after endOpenElement, no newline")
    void test087() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.endOpenElement(false);

        final String actual = htm.toString();
        assertEquals(">", actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after endOpenElement, newline")
    void test088() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.endOpenElement(true);

        final String actual = htm.toString();
        assertEquals(">\r\n", actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after closeEmptyElement, no newline")
    void test089() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.closeEmptyElement(false);

        final String actual = htm.toString();
        assertEquals("/>", actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after closeEmptyElement, newline")
    void test090() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.closeEmptyElement(true);

        final String actual = htm.toString();
        assertEquals("/>\r\n", actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after sP")
    void test094() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.sP();

        final String actual = htm.toString();
        assertEquals("<p>", actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after sP with class")
    void test095() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.sP(ABC);

        final String actual = htm.toString();
        assertEquals("<p class='abc'>", actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after sP with class and attributes")
    void test096() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.sP(ABC, ATTR_A_EQ_B, ATTR_C_EQ_D);

        final String actual = htm.toString();
        assertEquals("<p class='abc' a='b' c='d'>", actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after eP")
    void test097() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.eP();

        final String actual = htm.toString();
        assertEquals("</p>", actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after sSpan")
    void test098() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.sSpan(null);

        final String actual = htm.toString();
        assertEquals("<span>", actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after sSpan with class")
    void test099() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.sSpan(ABC);

        final String actual = htm.toString();
        assertEquals("<span class='abc'>", actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after sSpan with class and attributes")
    void test100() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.sSpan(ABC, ATTR_A_EQ_B, ATTR_C_EQ_D);

        final String actual = htm.toString();
        assertEquals("<span class='abc' a='b' c='d'>", actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after eSpan")
    void test101() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.eSpan();

        final String actual = htm.toString();
        assertEquals("</span>", actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after sH")
    void test102() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.sH(1);

        final String actual = htm.toString();
        assertEquals("<h1>", actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after eH")
    void test103() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.eH(2);

        final String actual = htm.toString();
        assertEquals("</h2>", actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after br")
    void test104() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.br();

        final String actual = htm.toString();
        assertEquals("<br/>", actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after sDiv")
    void test105() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.sDiv();

        final String actual = htm.toString();
        assertEquals(DIV_TAG, actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after sDiv with null class")
    void test106() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.sDiv(null);

        final String actual = htm.toString();
        assertEquals(DIV_TAG, actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after sDiv with class")
    void test107() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.sDiv(ABC);

        final String actual = htm.toString();
        assertEquals("<div class='abc'>", actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after sDiv with class and attributes")
    void test108() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.sDiv(ABC, ATTR_A_EQ_B, ATTR_C_EQ_D);

        final String actual = htm.toString();
        assertEquals("<div class='abc' a='b' c='d'>", actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after eDiv")
    void test109() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.eDiv();

        final String actual = htm.toString();
        assertEquals("</div>", actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after div")
    void test110() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.div(null);

        final String actual = htm.toString();
        assertEquals("<div></div>", actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after div with class")
    void test111() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.div(ABC);

        final String actual = htm.toString();
        assertEquals("<div class='abc'></div>", actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after div with class and attributes")
    void test112() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.div(ABC, ATTR_A_EQ_B, ATTR_C_EQ_D);

        final String actual = htm.toString();
        assertEquals("<div class='abc' a='b' c='d'></div>", actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after sTr")
    void test113() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.sTr();

        final String actual = htm.toString();
        assertEquals("<tr>", actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after eTr")
    void test114() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.eTr();

        final String actual = htm.toString();
        assertEquals("</tr>", actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after sTable")
    void test115() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.sTable();

        final String actual = htm.toString();
        assertEquals("<table>", actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after eTable")
    void test116() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.eTable();

        final String actual = htm.toString();
        assertEquals("</table>", actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after sTh")
    void test117() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.sTh();

        final String actual = htm.toString();
        assertEquals("<th>", actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after eTh")
    void test118() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.eTh();

        final String actual = htm.toString();
        assertEquals("</th>", actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after sTd")
    void test119() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.sTd();

        final String actual = htm.toString();
        assertEquals("<td>", actual, TOSTR_MISMATCH);
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("String value after eTd")
    void test120() {

        final HtmlBuilder htm = new HtmlBuilder(100);
        htm.eTd();

        final String actual = htm.toString();
        assertEquals("</td>", actual, TOSTR_MISMATCH);
    }
}
