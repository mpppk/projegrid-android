package com.projegrid.mobile.gridapp.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuki on 8/15/16.
 */
public abstract class GridAppParser {

    String dataStr;
    List<String> lines;

    abstract protected String getAppName();
    abstract protected List<String> getRequiredWords();
    abstract public String toJson();

    public GridAppParser(){}

    public GridAppParser(String dataStr) throws IOException {
        setDataStr(dataStr);
    }

    public void setDataStr(String dataStr) throws IOException {
        this.dataStr = dataStr;
        lines = toLines(dataStr);
    }

    /**
     * 与えられた文字列がParserによってparse可能かどうかを判定する
     * @return
     */
    public boolean canHandle(){
        for(String word : getRequiredWords()){
            if(!dataStr.contains(word)){
                return false;
            }
        }
        return true;
    }

    /**
     * 受け取った文字列を1行ごとのリストにして返す
     * @param dataStr
     * @return
     * @throws IOException
     */
    private List<String> toLines(String dataStr) throws IOException {
        BufferedReader br = new BufferedReader(new StringReader(dataStr));

        List<String> lines = new ArrayList<>();
        String line;
        while((line = br.readLine()) != null){
            lines.add(line);
        }

        if(lines.isEmpty()){
            throw new IllegalArgumentException(getAppName() + "のデータが空です。");
        }

        return lines;
    }

    public String parse(String dataStr){
        if(!this.canHandle()){
            throw new IllegalArgumentException(getAppName() + "ではないデータです。");
        }
        return toJson();
    }

    static public GridAppParser chooseParser(List<GridAppParser> parsers, String dataStr) throws IOException {
        for(GridAppParser parser : parsers){
            parser.setDataStr(dataStr);
            if(parser.canHandle()){
                return parser;
            }
        }

        throw new IllegalArgumentException("適切なparserが存在しません。");
    }
}
