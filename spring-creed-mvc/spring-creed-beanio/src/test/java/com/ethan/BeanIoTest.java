/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */
package com.ethan;

import com.ethan.beanio.vo.StudentDTO;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FixedLengthTokenizer;
import org.springframework.batch.item.file.transform.FormatterLineAggregator;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 8/25/2022 5:00 PM
 */
public class BeanIoTest {
    @Test
    void testBeanIo() throws Exception {
        LineMapper<StudentDTO> lineMapper = createStudentLineMapper();

        Object o = lineMapper.mapLine("Tony Tester|tony.teste1r@gmail.com|master", 1);
        if (o instanceof StudentDTO) {
            StudentDTO result = (StudentDTO) o;
            System.out.println(result);
        } else {
            System.out.println("not matched");
        }

        // lineMapper.

    }

    private LineMapper<StudentDTO> createStudentLineMapper() {
        DefaultLineMapper<StudentDTO> studentLineMapper = new DefaultLineMapper<>();

        LineTokenizer studentLineTokenizer = createStudentLineTokenizer();
        studentLineMapper.setLineTokenizer(studentLineTokenizer);

        FieldSetMapper<StudentDTO> studentInformationMapper =
                createStudentInformationMapper();
        studentLineMapper.setFieldSetMapper(studentInformationMapper);

        return studentLineMapper;
    }

    private LineTokenizer createStudentLineTokenizer() {
        /* DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setDelimiter("|");
        tokenizer.setStrict(false);
        tokenizer.setNames(new String[]{
                "name",
                "purchasedPackage",
                "emailAddress"
        }); */
        FixedLengthTokenizer tokenizer = new FixedLengthTokenizer();

        tokenizer.setStrict(false);
        String[] names = new String[] { "name", "purchasedPackage", "emailAddress", "customer.name" };
        tokenizer.setNames(names);
        Range[] ranges = new Range[] { new Range(1, 12), new Range(13, 15), new Range(16, 20), new Range(21, 29) };
        tokenizer.setColumns(ranges);

        // studentLineTokenizer.setFieldSetFactory();
        return tokenizer;
    }

    private FieldSetMapper<StudentDTO> createStudentInformationMapper() {
        BeanWrapperFieldSetMapper<StudentDTO> studentInformationMapper =
                new BeanWrapperFieldSetMapper<>();
        studentInformationMapper.setTargetType(StudentDTO.class);
        return studentInformationMapper;
    }

    @Value("/workspace/inputData*.csv")
    private Resource[] inputResources;

    private Resource outputResource = new FileSystemResource("/workspace/outputData.csv");

    @Test
    void writeBean() {
        //Create writer instance
        FlatFileItemWriter<StudentDTO> writer = new FlatFileItemWriter<>();

        //Set output file location
        writer.setResource(outputResource);

        //All job repetitions should "append" to same output file
        writer.setAppendAllowed(true);

        //Name field values sequence based on object properties
        // writer.setLineAggregator(new DelimitedLineAggregator<StudentDTO>() {
        //     {
        //         setDelimiter(",");
        //         setFieldExtractor(new BeanWrapperFieldExtractor<StudentDTO>() {
        //             {
        //                 setNames(new String[] { "name", "purchasedPackage", "emailAddress" });
        //             }
        //         });
        //     }
        // });
        // writer.setLineAggregator(new FormatterLineAggregator<StudentDTO>() {
        //     {
        //         setFieldExtractor(new BeanWrapperFieldExtractor<StudentDTO>() {
        //             {
        //                 setNames(new String[] { "name", "purchasedPackage", "emailAddress" });
        //             }
        //         });
        //         setFormat("%-8.8s%-12.12s%-11.11s%-16.16s");
        //     }
        // });
        // return writer;

        FormatterLineAggregator<StudentDTO> lineAggregator = new FormatterLineAggregator<>();

        BeanWrapperFieldExtractor<StudentDTO> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[]{"name", "purchasedPackage", "emailAddress"});

        lineAggregator.setFieldExtractor(fieldExtractor);
        lineAggregator.setFormat("%-8.8s%-12.12s%-11.11s");

        StudentDTO studentDTO = new StudentDTO();
        studentDTO.setName("1234567890");
        studentDTO.setEmailAddress("test@abc.com");
        studentDTO.setPurchasedPackage("ccc");

        System.out.println("==>"+lineAggregator.aggregate(studentDTO));
    }
}
