# LegalLens

LegalLens is a cutting-edge Android application designed to automate the summarization of legislative bills using advanced Natural Language Processing (NLP) techniques. The project aimed to compare several abstractive transformer-based models—Pegasus, ProphetNet, T5, and Longformer—along with an extractive-based technique, K-means, to identify the best performing model for integration into the mobile app. 
The BillSum dataset, consisting of 1,237 California bills and their reference summaries, was used for this project. Due to the vast size of the corpus and limited resources, we selected a subset of California bills for training, validation, and testing purposes. Specifically, we allocated 791 bills for training, 198 bills for validation, and 248 bills for testing. 
The transformer-based models were trained for 10 epochs, and their performance was evaluated using ROUGE scores as shown by the following table:
| column 1 | column 2 |
| :---:    | :---:    |
| data 1   | data 2   |


