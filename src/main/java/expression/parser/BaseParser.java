package expression.parser;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BaseParser {
    protected final Set<Character> LEXEMES;
    protected ExpressionSource source;
    private char currentLex;

    public BaseParser(Set<Character> LEXEMES) {
        this.LEXEMES = LEXEMES;
    }

    protected void nextChar() {
        currentLex = source.hasNext() ? source.next() : source.end();
    }

    protected String takeWordUntilLex() {
        return takeWord(currentLex -> !(Character.isWhitespace(currentLex) || LEXEMES.contains(currentLex)));
    }

    protected String takeUnaryOperationWord() {
        return takeWord(currentLex -> !(Character.isWhitespace(currentLex) || compare("\0", "(", "-")));
    }

    protected String takeWord(Function<Character, Boolean> stopCondition) {
        StringBuilder word = new StringBuilder();
        do {
            word.append(currentLex);
            nextChar();
        } while (stopCondition.apply(currentLex));
        return word.toString();
    }

    protected ExceptionParameters getExceptionParameters() {
        String next = takeWordUntilLex();
        return getExceptionParameters(next, source.getPosition() - next.length() - 1);
    }

    protected ExceptionParameters getExceptionParameters(String word, int position) {
        return new ExceptionParameters(word, position, source.getExpression());
    }

    protected String takeNumber() {
        StringBuilder num = new StringBuilder();
        while (Character.isDigit(currentLex) || currentLex == '.') {
            num.append(currentLex);
            nextChar();
        }
        return num.toString();
    }

    protected boolean compareAndSkip(String expected) {
        if (getSequence(expected.length()).equals(expected)) {
            return true;
        }
        rollBack(expected.length());
        return false;
    }

    protected boolean compare(String ... ch) {
        for (String c : ch) {
            String next = getSequence(c.length());
            rollBack(c.length());
            if (next.equals(c)) {
                return true;
            }
        }
        return false;
    }

    private String getSequence(int length) {
        StringBuilder next = new StringBuilder();
        for (int i = 0; i < length; i++) {
            next.append(currentLex);
            nextChar();
        }
        return next.toString();
    }

    protected boolean isDigit() {
        return Character.isDigit(currentLex);
    }

    protected boolean checkDouble(String number) {
        for (char c: number.toCharArray()) {
            if (c == '.') {
                return true;
            }
        }
        return false;
    }

    public char getCurrentLex() {
        return currentLex;
    }

    protected void skipWhitespaces() {
        while (hasNext() && Character.isWhitespace(currentLex)) {
            nextChar();
        }
    }

    protected boolean hasNext() {
        return source.hasNext() || currentLex != '\0';
    }

    protected boolean findLexeme(String c) {
        if (c.length() == 0) {
            return true;
            // end of the expression
        }
        if (c.length() == 1) {
            return LEXEMES.contains(c.charAt(0));
        }
        return false;
    }

    protected void rollBack(int v) {
        source.rollBack(v + 1);
        nextChar();
    }
}
