package CHART;

import model.Coviddata;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class LineChart extends JFrame {


    /**
     * Creates a new demo.
     *
     * @param title  the frame title.
     * @param selectedDataType
     *
     */
    public LineChart(final String title, List<Coviddata> resultList, List<Short> selectedDataType) {
        super(title);
        final CategoryDataset dataset = createDataset(resultList, selectedDataType);
        final JFreeChart chart = createChart(dataset, title);
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(600, 400));
        setContentPane(chartPanel);
    }

    /**
     * Creates a sample dataset.
     *
     * @return The dataset.
     */
    private CategoryDataset createDataset(List<Coviddata> resultList, List<Short> selectedDataType) {

        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy");

        if(selectedDataType.contains((short) 1)) {
            final String series1 = "confirmed";
            for(Coviddata cd : resultList) {
                if (cd.getDatakind() == 1) {
                    String date = format.format(cd.getTrndate());
                    dataset.addValue(cd.getQty(), series1, date);
                }
            }
        }
        if(selectedDataType.contains((short) 2)) {
            final String series2 = "deaths";
            for(Coviddata cd : resultList) {
                if (cd.getDatakind() == 2) {
                    String date = format.format(cd.getTrndate());
                    dataset.addValue(cd.getQty(), series2, date);
                }
            }
        }
        if(selectedDataType.contains((short) 3)) {
            final String series3 = "recovered";
            for(Coviddata cd : resultList) {
                if (cd.getDatakind() == 3) {
                    String date = format.format(cd.getTrndate());
                    dataset.addValue(cd.getQty(), series3, date);
                }
            }
        }

        return dataset;

    }

    private String chartTitle(String myTitle){
         final String title = myTitle;
         return title;
    }

    /**
     * Creates a sample chart.
     *
     * @param dataset  a dataset.
     *
     * @return The chart.
     */
    private JFreeChart createChart(final CategoryDataset dataset, final String myTitle) {

        // create the chart...
        final JFreeChart chart = ChartFactory.createLineChart(
                myTitle,       // chart title
                "Date",                    // domain axis label
                "Quantity",                   // range axis label
                dataset,                   // data
                PlotOrientation.VERTICAL,  // orientation
                true,                      // include legend
                true,                      // tooltips
                false                      // urls
        );

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
//        final StandardLegend legend = (StandardLegend) chart.getLegend();
        //      legend.setDisplaySeriesShapes(true);
        //    legend.setShapeScaleX(1.5);
        //  legend.setShapeScaleY(1.5);
        //legend.setDisplaySeriesLines(true);

        chart.setBackgroundPaint(Color.white);

        final CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setRangeGridlinePaint(Color.white);

        // customise the range axis...
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeAxis.setAutoRangeIncludesZero(true);

        // ****************************************************************************
        // * JFREECHART DEVELOPER GUIDE                                               *
        // * The JFreeChart Developer Guide, written by David Gilbert, is available   *
        // * to purchase from Object Refinery Limited:                                *
        // *                                                                          *
        // * http://www.object-refinery.com/jfreechart/guide.html                     *
        // *                                                                          *
        // * Sales are used to provide funding for the JFreeChart project - please    *
        // * support us so that we can continue developing free software.             *
        // ****************************************************************************

        // customise the renderer...
        final LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
//        renderer.setDrawShapes(true);

        renderer.setSeriesStroke(
                0, new BasicStroke(
                        2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                        1.0f, new float[] {10.0f, 6.0f}, 0.0f
                )
        );
        renderer.setSeriesStroke(
                1, new BasicStroke(
                        2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                        1.0f, new float[] {6.0f, 6.0f}, 0.0f
                )
        );
        renderer.setSeriesStroke(
                2, new BasicStroke(
                        2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                        1.0f, new float[] {2.0f, 6.0f}, 0.0f
                )
        );
        // OPTIONAL CUSTOMISATION COMPLETED.

        return chart;
    }

    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    //public static void main(final String[] args) {

    //    final LineChart demo = new LineChart("Line Chart Demo");
    //    demo.pack();
    //    RefineryUtilities.centerFrameOnScreen(demo);
    //    demo.setVisible(true);

    //}

}

