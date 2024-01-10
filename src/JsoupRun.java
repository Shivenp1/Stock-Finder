import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class JsoupRun {

    private static List<String> stockNames = new ArrayList<>();
    private static List<Double> stockPrices = new ArrayList<>();
    private static List<Double> stockChanges = new ArrayList<>();
    private static List<Double> stockRelativeVolumes = new ArrayList<>();
    private static List<Double> stockPEs = new ArrayList<>();
    private static List<Double> stockDividends = new ArrayList<>();
    private static List<Double> stockChangeWeekly = new ArrayList<>();

    public static List<String> findStocks(String preference, double threshold) {
        List<String> selectedStocks = new ArrayList<>();

        switch (preference.toLowerCase()) {
            case "safe":
                for (int i = 0; i < stockNames.size(); i++) {
                    if (stockPEs.get(i) < threshold) {
                        selectedStocks.add(stockNames.get(i));
                    }
                }
                break;

            default:
                System.out.println("Invalid preference");
        }

        return selectedStocks;
    }

    public static void main(String[] args) {
        fetchDataAndStore();

        Scanner scanner = new Scanner(System.in);

        System.out.println("What type of stock do you want to invest in? (e.g., safe)");
        String preference = scanner.nextLine();

        double threshold = 20;

        List<String> selectedStocks = findStocks(preference, threshold);

        System.out.println("Stocks matching your criteria:");
        for (String stock : selectedStocks) {
            System.out.println(stock);
        }
    }

    private static void fetchDataAndStore() {
        final String url = "https://www.tradingview.com/markets/stocks-usa/market-movers-large-cap/";

        try {
            final Document doc = Jsoup.connect(url).get();

            for (Element row : doc.select("table.table-Ngq2xrcG tr")) {
                if (row.select(".onscroll-shadow.cell-fixed-ZtyEm8a1.left-RLhfr_y4.cell-RLhfr_y4").text().equals("")) {
                    continue;
                } else {
                    final String name = row.select(".onscroll-shadow.cell-fixed-ZtyEm8a1.left-RLhfr_y4.cell-RLhfr_y4").text();

                    final String tempPrice = row.select("td.right-RLhfr_y4.cell-RLhfr_y4:nth-of-type(3)").text();
                    final String tempPrice1 = tempPrice.replace("USD", "");
                    final double price = Double.parseDouble(tempPrice1);

                    final String tempChange = row.select("td.right-RLhfr_y4.cell-RLhfr_y4:nth-of-type(4)").text();
                    final String tempChange2 = tempChange.replace("%", "");
                    final String tempChange3 = tempChange2.replaceAll("[^\\d.-]", "");
                    final String standardizedChange = tempChange3.startsWith("+") ? tempChange3.substring(1) : tempChange3;
                    final double change = Double.parseDouble(standardizedChange);

                    final String temprelativeVolume = row.select("td.right-RLhfr_y4.cell-RLhfr_y4:nth-of-type(6)").text();
                    final double relativeVolume = Double.parseDouble(temprelativeVolume);

                    final String tempPE = row.select("td.right-RLhfr_y4.cell-RLhfr_y4:nth-of-type(7)").text();
                    final String tempPE1 = tempPE.replace("—", "0");
                    final double PE = Double.parseDouble(tempPE1);

                    final String TempDiv = row.select("td.right-RLhfr_y4.cell-RLhfr_y4:nth-of-type(10)").text();
                    final String TempDiv1 = TempDiv.replace("%", "");
                    final double Div = Double.parseDouble(TempDiv1);

                    stockNames.add(name);
                    stockPrices.add(price);
                    stockChanges.add(change);
                    stockRelativeVolumes.add(relativeVolume);
                    stockPEs.add(PE);
                    stockDividends.add(Div);
                }
            }

            for (Element row : doc.select("table.table-Ngq2xrcG.tableSticky-SfGgNYTG tr")) {
                if (row.select("").text().equals("td.right-RLhfr_y4.cell-RLhfr_y4:nth-of-type(4)")) {
                    continue;
                } else {
                    final String tempChangeWeekly = row.select("td.right-RLhfr_y4.cell-RLhfr_y4:nth-of-type(4)").text();
                    final String tempChangeWeekly2 = tempChangeWeekly.replace("%", "");
                    final String tempChangeWeekly3 = tempChangeWeekly2.replaceAll("[^\\d.-]", "");
                    final String standardizedChange = tempChangeWeekly3.startsWith("+") ? tempChangeWeekly3.substring(1) : tempChangeWeekly3;
                    final double Weeklychange = Double.parseDouble(standardizedChange);
                    
                    stockChangeWeekly.add(Weeklychange);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}