package regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestingRegex {

    public static void main(String[] args) {
//        System.out.println(Pattern.matches("this", "this is an example this some more words"));
        regexExample("This is some sample text, more sample text", "");
    }

    public static void regexExample(String text, String patternString) {
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(text);

        System.out.println("Input text length: " + text.length());
        int foundIncrement = 0;


        while (matcher.find()) {
            System.out.println("Found: " + matcher.group() + " at index: " + matcher.start());
            foundIncrement++;
        }

        System.out.println("Finds: " + foundIncrement + " for pattern: " + pattern.pattern());
    }
}
