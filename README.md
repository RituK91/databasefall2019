# databasefall2019

Make sure you have the dataset : TestSample.xlsx
Then create the following empty excel workbook files under the same directory as TestSample.xlsx":

Inside a testFolder, create the following files:
subFact.xlsx, dataDC.xlsx, dataSC.xlsx, DownwardCarousel.xlsx, SidewardCarousel.xlsx,
DownwardCarouselRanking.xlsx, SidewardCarouselRanking.xlsx, FinalDownwardCarousel.xlsx, FinalSidewardCarousel.xlsx

Then execute the following steps in order:

1. Since it is a Maven project first do Maven clean and install.
2. Run the file CriticalRanking.java --> the file subFact.xlsx gets updated
3. Run the file PivotEntity.java --> files dataDC.xlsx, dataSC.xlsx gets updated
4. Run the file CarouselTitle.java --> files DownwardCarousel.xlsx, SidewardCarousel.xlsx gets updated
5. Run the file CarouselRanking.java --> files DownwardCarouselRanking.xlsx, SidewardCarouselRanking.xlsx gets updated
6. Run the file HighestCarouselRanking.java --> files FinalDownwardCarousel.xlsx, FinalSidewardCarousel.xlsx gets updated
7. To see the carousel members, facts run the file RepresentTheCarousels.java with the files DownwardCarousel.csv, 
SidewardCarousel.csv, subjectAndFact.csv. 
These csv files contain values from the generated carousel.
