import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class StopWatch {

    private final String name;
    private long start;
    private long end;
    private long duration;
    private int counter;
    private File file;
    private FileWriter output;
    private CSVWriter writer;

    public StopWatch(String name) {
        this.name = name;
        counter = 0;
        setup();
    }

    public void start() {
        start = System.nanoTime();
    }

    public void stop() {
        end = System.nanoTime();
        calculateDuration();
        convertToMs();
        counter++;
        writeToCSV();
    }

    private void calculateDuration() {
        duration = end - start;
    }

    private void convertToMs() {
        duration = TimeUnit.MILLISECONDS.convert(duration, TimeUnit.NANOSECONDS);
    }

    private void setup() {
        file = new File("./csv/" + name.trim() + ".csv");

        try {
            output = new FileWriter(file);
            writer = new CSVWriter(output,';', CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);

        } catch (IOException e) {
            e.printStackTrace();
        }
        writeHeader();
    }

    private void writeHeader() {
        String[] header = {"counter", "duration in ms"};
        writer.writeNext(header);
    }

    private void writeToCSV() {
        String[] data = {Integer.toString(counter), Long.toString(duration)};
        writer.writeNext(data);
    }

    public void print() {
        System.out.println(name + " finished in: " + duration + " ms");
    }
}