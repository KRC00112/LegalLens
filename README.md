# LegalLens

## About LegalLens

LegalLens is a cutting-edge Android application designed to automate the summarization of legislative bills using advanced Natural Language Processing (NLP) techniques. The project aimed to compare several abstractive transformer-based models—Pegasus, ProphetNet, T5, and Longformer—along with an extractive-based technique, K-means, to identify the best performing model for integration into the mobile app. 
The BillSum dataset, consisting of 1,237 California bills and their reference summaries, was used for this project. Due to the vast size of the corpus and limited resources, we selected a subset of California bills for training, validation, and testing purposes. Specifically, we allocated 791 bills for training, 198 bills for validation, and 248 bills for testing. 

## Model Results

The transformer-based models were trained for 10 epochs, and their performance was evaluated using ROUGE scores as shown by the following table:

| Models                                  | ROUGE-1 | ROUGE-2 | ROUGE-L | ROUGE-L_sum |
| :---:                                   | :---:   | :---:   | :---:   | :---:       |
| **google/pegasus-cnn_dailymail**        | 0.48    | 0.24    | 0.33    | 0.33        |
| **google-t5/t5-small**                  | 0.15    | 0.08    | 0.13    | 0.13        |
| **allenai/led-base-16384**              | 0.14    | 0.08    | 0.12    | 0.13        |
| **microsoft/prophetnet-large-uncased**  | 0.50    | 0.23    | 0.31    | 0.31        |

Additionally, the K-means extractive technique achieved the following ROUGE scores:
-	**ROUGE-1:** 0.3505
-	**ROUGE-2:** 0.0909
-	**ROUGE-L:** 0.2680
  
Among all models, Pegasus emerged as the top performer, demonstrating superior performance across all metrics. Due to its results, Pegasus was selected for implementation within LegalLens. By leveraging the power of Pegasus, LegalLens provides legal professionals, students, and researchers with an accessible and user-friendly tool to quickly generate summaries of extensive legal documents. 

## User Interface

### Authorization(Login and Registration)
<br/>
<p align="left">
  <img src="https://github.com/user-attachments/assets/1d77ce4b-bffb-4f42-9f82-fab551be1e22" alt="Login Image" width="300";"/>
  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
  <img src="https://github.com/user-attachments/assets/82a7429a-74c7-43be-ac66-d908af3b7d93" alt="Register Image" width="300";"/>
</p>


### Main Screen and Summarization History Screen
<br/>
 <p align="left">
  <img src="https://github.com/user-attachments/assets/cbeceb3a-be10-4880-870f-6afdf034fa6f" alt="Main Screen Image" width="300";"/>
  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
  <img src="https://github.com/user-attachments/assets/1ac390fe-7371-4675-9a9e-a71f090e0469" alt="history Image" width="300";"/>
</p>


### Settings
<br/>
 <p align="left">
  <img src="https://github.com/user-attachments/assets/f84b7f56-f88c-495a-a65f-421bb911f0a1" alt="settings Image" width="300";"/>
</p>

## Admin Interface

### Authorization(Login and Registration)
<br/>
<p align="left">
  <img src="https://github.com/user-attachments/assets/b47d18f1-df77-4372-b883-2d4fe87568d1" alt="Login Image" width="300";"/>
  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
  <img src="https://github.com/user-attachments/assets/c011cd21-74a0-45a4-8f7a-3c5ff5f03a29" alt="Register Image" width="300";"/>
</p>

*Note: Here, We use employee ID for authentication instead of a username, driven by sheer curiosity. Unlike users, whose authentication is done using email, employees' authentication is based on their own unique Employee ID.
The regex for employee ID is "LL20(2[4-9]|[3-9][0-9])(DEV|MAN)\d{4}". This pattern means that only those employees whose ID matches the regex can log in. The regex ensures the following:
- The ID starts with "LL20".
- Followed by a number between 24 and 99.
- Followed by either "DEV" or "MAN".
- Followed by exactly 4 digits.*


### Users' List & Details Screeens
<p align="left">
  <img src="https://github.com/user-attachments/assets/fbf6fa8b-b15b-403e-adce-2901497974a8" alt="listScreen Image" width="300"/>
  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
  <img src="https://github.com/user-attachments/assets/c130d2a6-d23c-482d-92cc-0391b0b75179" alt="details Image" width="300"/>
</p>


