# **LegalLens**

LegalLens is a cutting-edge Android application designed to automate the summarization of legislative bills using advanced Natural Language Processing (NLP) techniques. The project aimed to compare several abstractive transformer-based models—Pegasus, ProphetNet, T5, and Longformer—along with an extractive-based technique, K-means, to identify the best performing model for integration into the mobile app. 
The BillSum dataset, consisting of 1,237 California bills and their reference summaries, was used for this project. Due to the vast size of the corpus and limited resources, we selected a subset of California bills for training, validation, and testing purposes. Specifically, we allocated 791 bills for training, 198 bills for validation, and 248 bills for testing. 
The transformer-based models were trained for 10 epochs, and their performance was evaluated using ROUGE scores as shown by the following table:

| Models                              | ROUGE-1 | ROUGE-2 | ROUGE-L | ROUGE-L_sum |
| :---:                               | :---:   | :---:   | :---:   | :---:       |
| google/pegasus-cnn_dailymail        | 0.48    | 0.24    | 0.33    | 0.33        |
| google-t5/t5-small                  | 0.15    | 0.08    | 0.13    | 0.13        |
| allenai/led-base-16384              | 0.14    | 0.08    | 0.12    | 0.13        |
| microsoft/prophetnet-large-uncased  | 0.50    | 0.23    | 0.31    | 0.31        |

Additionally, the K-means extractive technique achieved the following ROUGE scores:
-	ROUGE-1: 0.3505
-	ROUGE-2: 0.0909
-	ROUGE-L: 0.2680
  
Among all models, Pegasus emerged as the top performer, demonstrating superior performance across all metrics. Due to its results, Pegasus was selected for implementation within LegalLens. By leveraging the power of Pegasus, LegalLens provides legal professionals, students, and researchers with an accessible and user-friendly tool to quickly generate summaries of extensive legal documents. 




