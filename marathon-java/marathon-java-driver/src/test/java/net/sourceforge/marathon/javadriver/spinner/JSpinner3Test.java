/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package net.sourceforge.marathon.javadriver.spinner;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import components.SpinnerDemo3;
import net.sourceforge.marathon.javadriver.JavaDriver;

@Test public class JSpinner3Test {

    private WebDriver driver;
    protected JFrame frame;
    private List<WebElement> spinners;
    private WebElement spinnerMonth;
    private WebElement spinnerYearUp;
    private WebElement spinnerYear;
    private WebElement spinnerDate;
    private WebElement spinnerYearDown;
    private WebElement spinnerMonthDown;
    private WebElement spinnerMonthUp;
    private WebElement spinnerDateUp;
    private WebElement spinnerMonthField;
    private WebElement spinnerDateField;
    private WebElement spinnerYearField;

    @BeforeMethod public void showDialog() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                frame = new JFrame(JSpinner3Test.class.getSimpleName());
                frame.setName("frame-" + JSpinner3Test.class.getSimpleName());
                frame.getContentPane().add(new SpinnerDemo3(false), BorderLayout.CENTER);
                frame.pack();
                frame.setAlwaysOnTop(true);
                frame.setVisible(true);
            }
        });

        driver = new JavaDriver();
        spinners = driver.findElements(By.cssSelector("spinner"));
        AssertJUnit.assertEquals(3, spinners.size());

        spinnerMonth = spinners.get(0);
        spinnerYear = spinners.get(1);
        spinnerDate = spinners.get(2);

        spinnerMonthUp = spinnerMonth.findElement(By.cssSelector("basic-arrow-button:nth(1)"));
        spinnerMonthDown = spinnerMonth.findElement(By.cssSelector("basic-arrow-button:nth(2)"));

        spinnerYearUp = spinnerYear.findElement(By.cssSelector("basic-arrow-button:nth(1)"));
        spinnerYearDown = spinnerYear.findElement(By.cssSelector("basic-arrow-button:nth(2)"));

        spinnerDateUp = spinnerDate.findElement(By.cssSelector("basic-arrow-button:nth(1)"));

        spinnerMonthField = spinnerMonth.findElement(By.cssSelector("formatted-text-field:nth(1)"));
        spinnerYearField = spinnerYear.findElement(By.cssSelector("formatted-text-field:nth(1)"));
        spinnerDateField = spinnerDate.findElement(By.cssSelector("formatted-text-field:nth(1)"));
    }

    @AfterMethod public void disposeDriver() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                frame.setVisible(false);
                frame.dispose();
            }
        });
        if (driver != null)
            driver.quit();
    }

    public void chooseValues() throws Throwable {
        AssertJUnit.assertEquals("true", spinnerMonthUp.getAttribute("enabled"));
        AssertJUnit.assertEquals("true", spinnerMonthDown.getAttribute("enabled"));
        AssertJUnit.assertEquals("January", spinnerMonth.getAttribute("value"));

        spinnerMonthDown.click();
        AssertJUnit.assertEquals("January", spinnerMonth.getAttribute("value"));

        String[] months = new java.text.DateFormatSymbols().getMonths();

        for (int i = 0; i < months.length - 1; i++) {
            AssertJUnit.assertEquals(months[i], spinnerMonth.getAttribute("value"));
            spinnerMonthUp.click();
        }

        for (int i = months.length - 2; i >= 0; i--) {
            AssertJUnit.assertEquals(months[i], spinnerMonth.getAttribute("value"));
            spinnerMonthDown.click();
        }
        AssertJUnit.assertEquals("January", spinnerMonth.getAttribute("value"));

        String sYear = spinnerYear.getAttribute("value");
        spinnerYearUp.click();
        Integer nextYear = Integer.parseInt(sYear) + 1;
        AssertJUnit.assertEquals(nextYear.toString(), spinnerYear.getAttribute("value"));
        spinnerYearDown.click();

        spinnerDateField.clear();
        spinnerDateField.sendKeys("01/2014");
        spinnerMonthField.click();
        AssertJUnit.assertEquals("01/2014", spinnerDateField.getText());

        for (int i = 0; i < months.length - 1; i++) {
            String[] split = spinnerDateField.getText().split("/");
            String monthValue = split[0];
            if (monthValue.equals("12")) {
                spinnerDateUp.click();
                AssertJUnit.assertEquals("01/2015", spinnerDateField.getText());
            }

            spinnerDateUp.click();
        }
    }

    public void sendKeys() throws Throwable {
        spinnerMonthField.clear();
        spinnerMonthField.sendKeys("NoMonth");
        spinnerYearField.click();
        AssertJUnit.assertEquals("January", spinnerMonth.getAttribute("value"));
        spinnerMonthField.clear();
        spinnerMonthField.sendKeys("June");
        spinnerYearField.click();
        AssertJUnit.assertEquals("June", spinnerMonth.getAttribute("value"));

        spinnerYearField.clear();
        String sYear = spinnerYear.getAttribute("value");
        spinnerYearField.sendKeys("20");
        spinnerMonthField.click();
        AssertJUnit.assertEquals(sYear, spinnerYear.getAttribute("value"));
        spinnerYearField.clear();
        spinnerYearField.sendKeys("2014");
        spinnerMonthField.click();
        AssertJUnit.assertEquals("2014", spinnerYear.getAttribute("value"));
        spinnerYearField.clear();
        spinnerYearField.sendKeys("2014a");
        spinnerMonthField.click();
        AssertJUnit.assertEquals("2014", spinnerYear.getAttribute("value"));
        spinnerYearField.clear();
        spinnerYearField.sendKeys("20142324");
        spinnerMonthField.click();
        AssertJUnit.assertEquals("2014", spinnerYear.getAttribute("value"));

        spinnerDateField.clear();
        spinnerDateField.sendKeys("07/2013");
        spinnerMonthField.click();
        spinnerDateField.clear();
        spinnerDateField.sendKeys("abc");
        spinnerMonthField.click();
        AssertJUnit.assertEquals("07/2013", spinnerDateField.getText());
        spinnerDateField.clear();
        spinnerDateField.sendKeys("01/07/2013");
        spinnerMonthField.click();
        AssertJUnit.assertEquals("07/2013", spinnerDateField.getText());
        spinnerDateField.clear();
        spinnerDateField.sendKeys("12/2013");
        spinnerMonthField.click();
        AssertJUnit.assertEquals("12/2013", spinnerDateField.getText());
    }

    public void color() throws Throwable {
        String[] months = new java.text.DateFormatSymbols().getMonths();

        spinnerDateField.clear();
        spinnerDateField.sendKeys("01/2014");
        spinnerMonthField.click();
        AssertJUnit.assertEquals("01/2014", spinnerDateField.getText());

        for (int i = 0; i < months.length - 1; i++) {
            String[] split = spinnerDateField.getText().split("/");
            String monthValue = split[0];

            if ((monthValue.equals("03")) || (monthValue.equals("04")) || (monthValue.equals("05"))) {
                AssertJUnit.assertEquals("[r=0,g=204,b=51]", spinnerDateField.getAttribute("foreground"));

            } else if ((monthValue.equals("06")) || (monthValue.equals("07")) || (monthValue.equals("08"))) {
                AssertJUnit.assertEquals("[r=255,g=0,b=0]", spinnerDateField.getAttribute("foreground"));

            } else if ((monthValue.equals("09")) || (monthValue.equals("10")) || (monthValue.equals("11"))) {
                AssertJUnit.assertEquals("[r=255,g=153,b=0]", spinnerDateField.getAttribute("foreground"));

            } else {
                AssertJUnit.assertEquals("[r=0,g=255,b=255]", spinnerDateField.getAttribute("foreground"));
            }

            spinnerDateUp.click();

        }
    }

}
