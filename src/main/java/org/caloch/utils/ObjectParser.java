package org.caloch.utils;
import java.io.StringReader;
import java.io.Reader;
import java.io.IOException;

import java.io.PushbackReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ObjectParser {

    public static String parseStringValue(Reader reader) throws IOException {
        skipUntilQuote(reader);
        return parseString(reader);
    }


    private static void skipUntilQuote(Reader reader) throws IOException {
        int ch;
        while ((ch = reader.read()) != -1 && ch != '"') {
        }
    }

    private static String parseString(Reader reader) throws IOException {
        StringBuilder sb = new StringBuilder();
        int ch;
        while ((ch = reader.read()) != -1) {
            if (ch == '"') {
                break;
            } else if (ch == '\\') {
                ch = reader.read();
                if (ch == '\\' || ch == '"' || ch == '/') {
                    sb.append((char) ch);
                } else {
                    sb.append('\\').append((char) ch);
                }
            } else {
                sb.append((char) ch);
            }
        }
        return sb.toString();
    }

    public static Double parseNumber(Reader reader, int cur) throws IOException {
        int ch;
        StringBuilder sb=new StringBuilder();
        sb.append((char) cur);
        while ((ch=reader.read())!=-1){
            if (ch == ',') {
                break;
            }
            if (ch == '}' || ch == ']') {
                ((PushbackReader) reader).unread(ch);
                break;
            }
            sb.append((char) ch);
        }
        return Double.parseDouble(sb.toString());
    }

    public static boolean parseBoolean(Reader reader, char ch) throws IOException {
        if (ch == 't') {
            reader.skip(3);
            return true;
        }else if (ch=='f'){
            reader.skip(4);
            return false;
        }
        return false;
    }


    public static List<Object> parseArray(Reader reader, int start) throws IOException {
        assert start == '[';
        List<Object> ret = new ArrayList<>();
        int cur;
        while ((cur = reader.read()) != -1) {
            if (cur == ']') break;
            if (cur == ',') cur = reader.read();
            ret.add(parseValue(reader, cur));
        }
        return ret;
    }


    public static Object parseValue(Reader reader, int cur) throws IOException {
        if (cur == '[') return parseArray(reader, cur);
        if(cur=='{')return parseObj(reader,cur);
        if (cur == 't' || cur == 'f') return parseBoolean(reader, (char)cur);
        else if (cur == '\"') return parseString(reader);
        else return parseNumber(reader, cur);
    }


    static Map<String, Object> parseObj(Reader sr,int cur) throws IOException {
        assert cur==(int)'{';
        Map<String, Object> ret = new HashMap<>();
        int ch;
        while ((ch = sr.read()) != -1) {
            if (ch == '}') break;
            if (ch == ',') {
                ch = sr.read();
            }
            String key = parseString(sr);
            sr.read();
            Object value = parseValue(sr, sr.read());
            ret.put(key, value);
        }
        return ret;
    }

}