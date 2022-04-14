package com.healthycoderapp;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BMICalculatorTest {

    private static String environment = "prod";

    @BeforeAll
    static void beforeAll() {
        System.out.println("Before all unit tests.");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("After all unit tests.");
    }

    @Nested
    class isDietRecommendedTests {

        @ParameterizedTest
        @ValueSource(doubles = {100.0, 89.0, 95.0, 110.0})
        void should_returnTrue_when_isDietRecommended(Double coderWeight) {
            //given
            double weight = coderWeight;
            double height = 1.72;

            //when
            boolean isDietRecommended = BMICalculator.isDietRecommended(weight, height);

            //then
            assertTrue(isDietRecommended);
        }

        @ParameterizedTest(name = "weight={0}, height={1}")
        @CsvSource(value = {"89.0, 1,72", "95.0, 1.75", "110.0, 1.78"})
        void should_returnTrue_when_isDietRecommended_CsvSource(Double coderWeight, Double coderHeight) {
            //given
            double weight = coderWeight;
            double height = coderHeight;

            //when
            boolean isDietRecommended = BMICalculator.isDietRecommended(weight, height);

            //then
            assertTrue(isDietRecommended);
        }

        @ParameterizedTest(name = "weight={0}, height={1}")
        @CsvFileSource(resources = "/diet-recommended-input-data.csv", numLinesToSkip = 1)
        void should_returnTrue_when_isDietRecommended_CsvFileSource(Double coderWeight, Double coderHeight) {
            //given
            double weight = coderWeight;
            double height = coderHeight;

            //when
            boolean isDietRecommended = BMICalculator.isDietRecommended(weight, height);

            //then
            assertTrue(isDietRecommended);
        }

        @Test
        void should_returnTrue_when_isDietNotRecommended() {
            //given
            double weight = 80.0;
            double height = 1.8;

            //when
            boolean isDietRecommended = BMICalculator.isDietRecommended(weight, height);

            //then
            assertFalse(isDietRecommended);
        }

        @Test
        void should_throwArithmeticException_when_heightIsZero() {
            //given
            double weight = 80.0;
            double height = 0;

            //when
            Executable executable = () -> BMICalculator.isDietRecommended(weight, height);

            //then
            assertThrows(ArithmeticException.class, executable);
        }

    }

    @Nested
    class FindCoderWithWorstBMITests {

        @Test
        void should_returnCoderWithWorstBMI_when_coderListNotEmpty() {
            //given
            List<Coder> coders = Arrays.asList(new Coder(1.80, 60.0),
                    new Coder(1.82, 98.0),
                    new Coder(1.82, 64.7));

            //when
            Coder coderWorstBMI = BMICalculator.findCoderWithWorstBMI(coders);

            //then
            assertAll(
                    () -> assertEquals(1.82, coderWorstBMI.getHeight()),
                    () -> assertEquals(98.0, coderWorstBMI.getWeight())
            );
        }

        @Test
        void should_returnNullWorstBMICoder_when_coderListEmpty() {
            //given
            List<Coder> coders = Collections.emptyList();

            //when
            Coder coder = BMICalculator.findCoderWithWorstBMI(coders);

            //then
            assertNull(coder);
        }

        @Test
        void should_returnCorrectBMIScoreArray_when_CoderListNotEmpty() {
            //given
            List<Coder> coders = Arrays.asList(new Coder(1.80, 60.0),
                    new Coder(1.82, 98.0),
                    new Coder(1.82, 64.7));
            double[] expected = {18.52, 29.59, 19.53};

            //when
            double[] bmiScores = BMICalculator.getBMIScores(coders);

            //then
            assertArrayEquals(expected, bmiScores);
        }

        @Test
        void should_ReturnCoderWithWorstBMIIn1Ms_When_CoderListHas10000Elements() {

            //given
            Assumptions.assumeTrue(BMICalculatorTest.environment.equals("prod"));
            List<Coder> coders = new ArrayList<>();
            for (int i = 0; i < 10000; i++)
                coders.add(new Coder(1.0 + i, 10.0 + i));

            //when
            Executable executable = () -> BMICalculator.findCoderWithWorstBMI(coders);

            //then
            assertTimeout(Duration.ofMillis(100), executable);

        }

    }

}