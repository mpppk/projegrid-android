package com.projegrid.mobile.gridapp.parser;

import android.util.Log;

import com.projegrid.mobile.gridapp.model.GridAppModel;
import com.projegrid.mobile.gridapp.model.YTrainGridAppModel;

import org.joda.time.DateTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by yuki on 8/15/16.
 */
public class YTrainGridAppParser extends GridAppParser {
    private static final String TAG = "YTrainGridAppParser";

    private static final String ARROW_STR = " ⇒ ";

    private final String INVALID_DATA_MESSAGE = getAppName() + "に渡されたデータが想定と異なります。";

    public YTrainGridAppParser(String dataStr) throws IOException {
        super(dataStr);
    }

    public YTrainGridAppParser(){
        super();
    }

    @Override
    public String getAppName() {
        return "Yahoo!乗り換え案内";
    }

    @Override
    public List<String> getRequiredWords() {
        return new ArrayList<>(Arrays.asList(
                "※定期代が含まれた検索結果は個人の定期区間に依存するため、上記の文面やリンク先の経路・料金が、送信元と受取先で一致しない場合がございますのでご注意ください。",
                "[Yahoo!乗換案内]"));
    }

    @Override
    public GridAppModel createModel() {
        Log.d(TAG, dataStr);
        YTrainGridAppModel model = new YTrainGridAppModel();
        String[] stations = getDepartureAndArrivalStation(lines.get(0));
        model.setDepartureStation(stations[0]);
        model.setArrivalStation(stations[1]);

        DateTime[] dateTimes = getDepartureAndArrivalTime(lines.get(1), lines.get(2));
        model.setDepartureTime(dateTimes[0]);
        model.setArrivalTime(dateTimes[1]);
        Log.d(TAG, "departure date time: " + dateTimes[0]);
        Log.d(TAG, "arrival date time: " + dateTimes[1]);

        return model;
    }

    private String[] getDepartureAndArrivalStation(String departureAndArrivalStationLine){
        if(!departureAndArrivalStationLine.contains(ARROW_STR)){
            throw new IllegalArgumentException(INVALID_DATA_MESSAGE + "(1行目が[出発駅] ⇒ [到着駅]ではありません。)");
        }

        String[] departureAndArriveStationArray = departureAndArrivalStationLine.split(ARROW_STR);

        if(departureAndArriveStationArray.length < 2 || departureAndArriveStationArray.length > 2){
            throw new IllegalArgumentException(INVALID_DATA_MESSAGE + "(1行目が[出発駅] ⇒ [到着駅]ではありません。矢印(⇒)が見つからないか、複数見つかりました。)");
        }

        if(departureAndArriveStationArray[0].equals("") || departureAndArriveStationArray[1].equals("")){
            throw new IllegalArgumentException(INVALID_DATA_MESSAGE + "(1行目が[出発駅] ⇒ [到着駅]ではありません。駅名が空です。)");
        }

        return departureAndArriveStationArray;
    }

    private DateTime[] getDepartureAndArrivalTime(String dateStr, String departureAndArrivalTimeLine){
        String[] timeStrArray = departureAndArrivalTimeLine.split(ARROW_STR);
        String departureDateTimeStr = dateStr.replaceAll("年|月", "-").replace("日", "") + "T" + timeStrArray[0] + ":00";
        String arrivalDateTimeStr = dateStr.replaceAll("年|月", "-").replace("日", "") + "T" + timeStrArray[1] + ":00";

        DateTime[] dateTimes = new DateTime[2];
        dateTimes[0] = DateTime.parse(departureDateTimeStr);
        dateTimes[1] = DateTime.parse(arrivalDateTimeStr);
        return dateTimes;
    }
}
