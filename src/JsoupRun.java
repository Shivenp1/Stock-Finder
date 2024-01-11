import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class JsoupRun {

    private static List<String> stockNames = new ArrayList<>();
    private static List<Double> stockPrices = new ArrayList<>();
    private static List<Double> stockChanges = new ArrayList<>();
    private static List<Double> stockRelativeVolumes = new ArrayList<>();
    private static List<Double> stockPEs = new ArrayList<>();
    private static List<Double> stockDividends = new ArrayList<>();
    private static List<Double> stockChangeWeekly = new ArrayList<>();
    private static List<Double> stockChangeMonthly = new ArrayList<>();
    private static List<Double> stockChangeYearly = new ArrayList<>();
    private static List<Double> stockVolatility = new ArrayList<>();
    

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
                
            case "biggest movers":
                // Create a list of indices sorted based on the change percentage in descending order
                List<Integer> sortedIndices = IntStream.range(0, stockNames.size())
                        .boxed()
                        .sorted(Comparator.comparingDouble(i -> -stockChanges.get(i)))
                        .collect(Collectors.toList());

                int topN = Math.min(5, stockNames.size());
                for (int i = 0; i < topN; i++) {
                    selectedStocks.add(stockNames.get(sortedIndices.get(i)) + " - Change: " + stockChanges.get(sortedIndices.get(i)) + "%");
                }
                break;
                
            case "highest volume":
                // Create a list of indices sorted based on relative volume in descending order
                List<Integer> volumeSortedIndices = IntStream.range(0, stockNames.size())
                        .boxed()
                        .sorted(Comparator.comparingDouble(i -> -stockRelativeVolumes.get(i)))
                        .collect(Collectors.toList());

                // Display the top 10 stocks with the highest relative volume
                int topNVolume = Math.min(5, stockNames.size());
                for (int i = 0; i < topNVolume; i++) {
                    selectedStocks.add(stockNames.get(volumeSortedIndices.get(i)) + " - Relative Volume: " + stockRelativeVolumes.get(volumeSortedIndices.get(i)));
                }
                break;
                
            case "highest dividend":
                List<Integer> dividendSortedIndices = IntStream.range(0, stockNames.size())
                        .boxed()
                        .sorted(Comparator.comparingDouble(i -> -stockDividends.get(i)))
                        .collect(Collectors.toList());

                // Display the top 5 stocks with the highest dividend
                int topNDividend = Math.min(5, stockNames.size());
                for (int i = 0; i < topNDividend; i++) {
                    selectedStocks.add(stockNames.get(dividendSortedIndices.get(i)) + " - Dividend: " + stockDividends.get(dividendSortedIndices.get(i)) + "%");
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

    	    // Define menu options
    	    List<String> menuOptions = Arrays.asList("Safe Stocks", "Biggest Movers", "Highest Volume", "Highest Dividend");

    	    // Display menu to the user
    	    System.out.println("Select the type of stock you want to invest in:");
    	    for (int i = 0; i < menuOptions.size(); i++) {
    	        System.out.println((i + 1) + ". " + menuOptions.get(i));
    	    }

    	    // Prompt the user to choose an option
    	    System.out.print("Enter the number of your choice: ");
    	    int choice = scanner.nextInt();


    	    // Process the user's choice
    	    String preference;
    	    switch (choice) {
    	        case 1:
    	            preference = "safe";
    	            break;
    	        case 2:
    	            preference = "biggest movers";
    	            break;
    	        case 3:
    	            preference = "highest volume";
    	            break;
    	        case 4:
    	            preference = "Highest Dividend";
    	            break;
    	        default:
    	            System.out.println("Invalid choice. Exiting.");
    	            return;
    	    }

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
                    
                    final String tempChangeMonthly = row.select("td.right-RLhfr_y4.cell-RLhfr_y4:nth-of-type(5)").text();
                    final String tempChangeMonthly2 = tempChangeMonthly.replace("%", "");
                    final String tempChangeMonthly3 = tempChangeMonthly2.replaceAll("[^\\d.-]", "");
                    final String standardizedChange1 = tempChangeMonthly3.startsWith("+") ? tempChangeMonthly3.substring(1) : tempChangeMonthly3;
                    final double MonthlyChange = Double.parseDouble(standardizedChange1);
                    
                    stockChangeMonthly.add(MonthlyChange);
                    
                    final String tempChangeyearly = row.select("td.right-RLhfr_y4.cell-RLhfr_y4:nth-of-type(9)").text();
                    final String tempChangeyearly2 = tempChangeyearly.replace("%", "");
                    final String tempChangeyearly3 = tempChangeyearly2.replaceAll("[^\\d.-]", "");
                    final String standardizedChange2 = tempChangeyearly3.startsWith("+") ? tempChangeyearly3.substring(1) : tempChangeyearly3;
                    final double YearlyChange = Double.parseDouble(standardizedChange2);
                    
                    stockChangeYearly.add(YearlyChange);
                    
                    final String tempVolatility = row.select("td.right-RLhfr_y4.cell-RLhfr_y4:nth-of-type(9)").text();
                    final String tempVolatility2 =  tempVolatility.replace("%", "");
                    final double Volatility = Double.parseDouble(tempVolatility2);
                    
                    stockVolatility.add(Volatility);
                    
                    
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}