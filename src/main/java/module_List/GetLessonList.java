package module_List;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
//import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.Test;

public class GetLessonList {

	@Test
	public void GetModulesList() throws IOException {

		String URL = "https://www.udemy.com/course/selenium-webdriver-web-based-automation-testing/";
		ChromeOptions co = new ChromeOptions();
		co.addArguments("--disable-infobars");
		co.addArguments("--disable-notifications");
		co.setAcceptInsecureCerts(true);

		System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "\\drivers\\chromedriver.exe");
		WebDriver driver = new ChromeDriver(co);

		//driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		//driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(20));
		driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);

		// To maximize the Browser
		driver.manage().window().maximize();

		// Open the URL with Selenium
		driver.get(URL);

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Expand all modules and lessons
		driver.findElement(By.xpath("//button[@data-purpose='expand-toggle']")).click();
		String filepath = System.getProperty("user.dir") + "\\Modulelist.xlsx";
		File src = new File(filepath);

		FileInputStream fis = new FileInputStream(src);

		XSSFWorkbook workbook = new XSSFWorkbook(fis);

		XSSFSheet sheet = workbook.getSheetAt(0);

		// Get list of all modules
		List<WebElement> moduleList = driver.findElements(By.xpath("//span[@class='section--section-title--wcp90']"));
		// System.out.println("Number of Modules are : " + moduleList.size());
		int rowNum = 0;
		int moduleNum = 0;
		String ParentXpath = "//div[@class='accordion-panel-module--content-wrapper--DIUt_']";

		for (WebElement ModuleName : moduleList) {
			String ModuleNameText = ModuleName.getText();

			if (!ModuleNameText.isEmpty()) {
				// System.out.println(rowNum + " " + ModuleNameText);
				moduleNum++;
				if (sheet.getRow(rowNum) == null)
					sheet.createRow(rowNum);
				sheet.getRow(rowNum).createCell(0);
				sheet.getRow(rowNum).getCell(0).setCellValue("Module" + moduleNum + " " + ModuleNameText);
				rowNum++;

				// Get list of all lessons per module
				String LessonsXpath = "(" + ParentXpath + ")[" + moduleNum + "]//div[@class='section--row--3sLRB']";
				List<WebElement> LessonsList = driver.findElements(By.xpath(LessonsXpath));
				// System.out.println("Number of Lessons are : " + LessonsList.size());

				for (WebElement LessonName : LessonsList) {
					String LessonNameText = LessonName.getText();

					if (!LessonNameText.isEmpty()) {
						// System.out.println(LessonNameText);
						if (sheet.getRow(rowNum) == null)
							sheet.createRow(rowNum);
						sheet.getRow(rowNum).createCell(0);
						sheet.getRow(rowNum).getCell(0).setCellValue(LessonNameText);
						rowNum++;
					}
				}

			}
		}

		try {
			FileOutputStream writeFile = new FileOutputStream(
					filepath);
			workbook.write(writeFile);
			workbook.close();
			writeFile.close();
			System.out.println("Module xls file is being created Successfully");

		} catch (FileNotFoundException e) {
			// Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}

		// To Close the Browser
		driver.quit();

	}
}
