package com.jonbore.flink.batch;


import com.jonbore.flink.config.WordCountData;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.utils.MultipleParameterTool;
import org.apache.flink.util.Collector;
import org.apache.flink.util.Preconditions;

public class WordCount {
    public static void main(String[] args) throws Exception {
        final MultipleParameterTool params = MultipleParameterTool.fromArgs(args);

        // set up the execution environment
        //remote commit
        //final ExecutionEnvironment env = ExecutionEnvironment.createRemoteEnvironment("119.3.251.41", 8081, "D:\\code\\Local\\Spring-Parent\\flink\\target\\original-flink-1.0.0.jar");
        //local or cluster
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        // make parameters available in the web interface
        env.getConfig().setGlobalJobParameters(params);
        env.setParallelism(1);
        // get input data
        DataSet<String> text = null;
        if (params.has("input")) {
            // union all the inputs from text files
            for (String input : params.getMultiParameterRequired("input")) {
                if (text == null) {
                    text = env.readTextFile(input);
                } else {
                    text = text.union(env.readTextFile(input));
                }
            }
            Preconditions.checkNotNull(text, "Input DataSet should not be null.");
        } else {
            // get default test text data
            System.out.println("Executing WordCount example with default input data set.");
            System.out.println("Use --input to specify file input.");
            text = WordCountData.getDefaultTextLineDataSet(env);
        }

        DataSet<Tuple3<String, Integer, String>> counts =
                // split up the lines in pairs (2-tuples) containing: (word,1)
                text.flatMap(new Tokenizer())
                        // group by the tuple field "0" and sum up tuple field "1"
                        .groupBy(0)
                        .sum(1);

        // emit result
        if (params.has("output")) {
            counts.writeAsCsv(params.get("output"), "\n", " ");
            // execute program
            env.execute("WordCount Example");
        } else {
            System.out.println("Printing result to stdout. Use --output to specify output path.");
            counts.print();
        }
    }

    // *************************************************************************
    //     USER FUNCTIONS
    // *************************************************************************

    /**
     * Implements the string tokenizer that splits sentences into words as a user-defined
     * FlatMapFunction. The function takes a line (String) and splits it into multiple pairs in the
     * form of "(word,1)" ({@code Tuple2<String, Integer>}).
     */
    public static final class Tokenizer
            implements FlatMapFunction<String, Tuple3<String, Integer, String>> {

        @Override
        public void flatMap(String value, Collector<Tuple3<String, Integer, String>> out) {
            // normalize and split the line
            String[] tokens = value.toLowerCase().split("\\W+");

            // emit the pairs
            for (String token : tokens) {
                if (token.length() > 0) {
                    out.collect(new Tuple3<String, Integer, String>(token, 1, token.length() + ""));
                }
            }
        }
    }
}
