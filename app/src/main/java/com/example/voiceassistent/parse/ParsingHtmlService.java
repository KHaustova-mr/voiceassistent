package com.example.voiceassistent.parse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class ParsingHtmlService {
    private static final String URL = "http://mirkosmosa.ru/holiday/2020";

    public static String getHoliday(String date){
        Document document = null;
        try {
            document = Jsoup.connect(URL).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(document == null)
            return "Не удалось найти праздники";

        Elements dayElements = document.select(".month_row");
        for(Element dayElement: dayElements){
            String dateElement = dayElement.selectFirst(".month_cel_date > span").text();
            if(date.equals(dateElement)){
                String result = "";
                Elements holidayElements = dayElement.selectFirst(".holiday_month_day_holiday").select("a");
                int i = 0;
                for(Element holidayElement : holidayElements){
                    if(i != 0) result += ", ";
                    result += holidayElement.text();
                    i++;
                }
                if(result == "")
                    return "В этот день нет праздников";
                return result;
            }
        }

        return "Не удалось найти праздники";
    }
}
