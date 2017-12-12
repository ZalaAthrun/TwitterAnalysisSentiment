import configuration.Environment;
import core.NaiveBayesian;
import entity.Word;
import io.*;
import java.io.IOException;

public class MainApplication {
    public static void main(String[] args) {

        String environment = null;

        Reader reader = null;
        Writer writer = null;

        if(args.length<3){
            environment = Environment.ENV;
            reader = new LocalFileReader();
            writer = new LocalFileWriter();
        }else{
            environment = "hadoop";
            reader = new HdfsFileReader();
            writer = new HdfsFileWriter();
        }

        String training = "";
        String testing = "";
        try {
            if(environment.equalsIgnoreCase(Environment.ENV)){
                training = reader.load("data/data_training.txt");
                testing = reader.load("data/data_testing.txt");
            }else if(environment.equalsIgnoreCase("hadoop")){
                training = reader.load(args[0]);
                testing = reader.load(args[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        NaiveBayesian.Mapper mapper = new NaiveBayesian.Mapper();
        NaiveBayesian.Reducer reducer = new NaiveBayesian.Reducer();

        String output = "result:actual:sentence\n";
        for(Word word : reducer.calculate(testing,reducer.reduce(
                        mapper.map(training)
        ))){
            output+=(word.getSentiment().getDescription()+":"+word.getContent()+"\n");
        }
        try {
            if(environment.equalsIgnoreCase(Environment.ENV)){
                writer.Write("data/output.txt",output);
            }else if(environment.equalsIgnoreCase("hadoop")){
                writer.Write(args[2],output);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
