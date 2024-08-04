import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class JsoupRun {

    private static List<String> stockNames = new ArrayList<>();
    private static List<Double> stockPrices = new ArrayList<>();
    private static List<Double> stockChanges = new ArrayList<>();
    private static List<Double> stockRelativeVolumes = new ArrayList<>();
    private static List<Double> stockPEs = new ArrayList<>();
    private static List<Double> stockDividends = new ArrayList<>();

    public static List<String> findStocks(String preference, double threshold) {
        List<String> selectedStocks = new ArrayList<>();

        if (preference.equalsIgnoreCase("safe stocks")) {
            for (int i = 0; i < stockNames.size(); i++) {
                if (stockPEs.get(i) < threshold) {
                    selectedStocks.add(stockNames.get(i));
                }
            }
        } else if (preference.equalsIgnoreCase("biggest movers")) {
            List<Integer> sortedIndices = IntStream.range(0, stockNames.size())
                    .boxed()
                    .sorted(Comparator.comparingDouble(i -> -stockChanges.get(i)))
                    .collect(Collectors.toList());

            for (int i = 0; i < Math.min(5, stockNames.size()); i++) {
                selectedStocks.add(stockNames.get(sortedIndices.get(i)) + " - Change: " + stockChanges.get(sortedIndices.get(i)) + "%");
            }
        } else if (preference.equalsIgnoreCase("highest volume")) {
            List<Integer> volumeSortedIndices = IntStream.range(0, stockNames.size())
                    .boxed()
                    .sorted(Comparator.comparingDouble(i -> -stockRelativeVolumes.get(i)))
                    .collect(Collectors.toList());

            for (int i = 0; i < Math.min(5, stockNames.size()); i++) {
                selectedStocks.add(stockNames.get(volumeSortedIndices.get(i)) + " - Relative Volume: " + stockRelativeVolumes.get(volumeSortedIndices.get(i)));
            }
        } else if (preference.equalsIgnoreCase("highest dividend")) {
            List<Integer> dividendSortedIndices = IntStream.range(0, stockNames.size())
                    .boxed()
                    .sorted(Comparator.comparingDouble(i -> -stockDividends.get(i)))
                    .collect(Collectors.toList());

            for (int i = 0; i < Math.min(5, stockNames.size()); i++) {
                selectedStocks.add(stockNames.get(dividendSortedIndices.get(i)) + " - Dividend: " + stockDividends.get(dividendSortedIndices.get(i)) + "%");
            }
        } else {
            System.out.println("Invalid preference");
        }

        return selectedStocks;
    }

    public static void main(String[] args) {
        fetchDataAndStore();

        JFrame frame = new JFrame("Stock Analysis Tool");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel label = new JLabel("Select the type of stock you want to invest in:");
        panel.add(label);

        String[] options = {"Safe Stocks", "Biggest Movers", "Highest Volume", "Highest Dividend"};
        JComboBox<String> comboBox = new JComboBox<>(options);
        panel.add(comboBox);

        JButton button = new JButton("Find Stocks");
        panel.add(button);

        JTextArea textArea = new JTextArea(10, 40);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        panel.add(scrollPane);

        button.addActionListener(e -> {
            String preference = (String) comboBox.getSelectedItem();
            double threshold = 20;
            List<String> selectedStocks = findStocks(preference, threshold);
            textArea.setText("");
            for (String stock : selectedStocks) {
                textArea.append(stock + "\n");
            }
        });

        frame.getContentPane().add(panel);
        frame.setVisible(true);
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
                    final String tempPrice1 = tempPrice.replace("USD", "").replace(",", "");
                    final double price = Double.parseDouble(tempPrice1);

                    final String tempChange = row.select("td.right-RLhfr_y4.cell-RLhfr_y4:nth-of-type(4)").text();
                    final String tempChange2 = tempChange.replace("%", "").replaceAll("[^\\d.-]", "");
                    final double change = Double.parseDouble(tempChange2);

                    final String tempRelativeVolume = row.select("td.right-RLhfr_y4.cell-RLhfr_y4:nth-of-type(6)").text();
                    final double relativeVolume = Double.parseDouble(tempRelativeVolume);

                    final String tempPE = row.select("td.right-RLhfr_y4.cell-RLhfr_y4:nth-of-type(7)").text();
                    final String tempPE1 = tempPE.replace("—", "0");
                    final double PE = Double.parseDouble(tempPE1);

                    final String tempDiv = row.select("td.right-RLhfr_y4.cell-RLhfr_y4:nth-of-type(10)").text();
                    final String tempDiv1 = tempDiv.replace("%", "");
                    final double Div = Double.parseDouble(tempDiv1);

                    stockNames.add(name);
                    stockPrices.add(price);
                    stockChanges.add(change);
                    stockRelativeVolumes.add(relativeVolume);
                    stockPEs.add(PE);
                    stockDividends.add(Div);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
