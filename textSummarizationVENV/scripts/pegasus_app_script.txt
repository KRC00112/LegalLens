from flask import Flask, render_template, request, jsonify
from transformers import PegasusForConditionalGeneration, PegasusTokenizer
import torch

app = Flask(__name__)

model_name = "ruchita1010/pegasuscnn-dailymail_billsum_model"
tokenizer = PegasusTokenizer.from_pretrained("ruchita1010/pegasuscnn-dailymail_billsum_model")

device = "cuda" if torch.cuda.is_available() else "cpu"
model = PegasusForConditionalGeneration.from_pretrained("ruchita1010/pegasuscnn-dailymail_billsum_model")


@app.route('/', methods=['GET'])
def home():
    return render_template('index.html')

max_length = 70

@app.route('/text-summarization', methods=["POST"])
def summarize():
    if request.method == "POST":
        text = request.form["inputtext_"]
        quartile = len(text) // 4
        string_1 = "".join(text[0:quartile])
        string_2 = "".join(text[quartile: quartile*2])
        string_3 = "".join(text[quartile*2:quartile*3])
        string_4 = "".join(text[quartile*3:quartile*4])
        string_1, string_2, string_3, string_4 = str(string_1), str(string_2), str(string_3), str(string_4)

        # String 1
        input_text = "summarize: " + string_1
        tokenized_text = tokenizer.encode(input_text, return_tensors='pt', max_length=512).to(device)
        summary_ = model.generate(tokenized_text, min_length=30, max_length=max_length)
        summary_1 = tokenizer.decode(summary_[0], skip_special_tokens=True)

        # String 2
        input_text = "summarize: " + string_2
        tokenized_text = tokenizer.encode(input_text, return_tensors='pt', max_length=512).to(device)
        summary_ = model.generate(tokenized_text, min_length=30, max_length=max_length)
        summary_2 = tokenizer.decode(summary_[0], skip_special_tokens=True)

        # String 3
        input_text = "summarize: " + string_3
        tokenized_text = tokenizer.encode(input_text, return_tensors='pt', max_length=512).to(device)
        summary_ = model.generate(tokenized_text, min_length=30, max_length=max_length)
        summary_3 = tokenizer.decode(summary_[0], skip_special_tokens=True)

        # String 4
        input_text = "summarize: " + string_4
        tokenized_text = tokenizer.encode(input_text, return_tensors='pt', max_length=512).to(device)
        summary_ = model.generate(tokenized_text, min_length=30, max_length=max_length)
        summary_4 = tokenizer.decode(summary_[0], skip_special_tokens=True)

        summary = summary_1 + summary_2 + summary_3 + summary_4
        sentences = summary.split('.')

        # Remove the last sentence
        if len(sentences) > 1:
            modified_summary = '.'.join(sentences[:-1])
            modified_summary = modified_summary + '.'
        else:
            pass

        return jsonify({"summary": modified_summary})


if __name__ == '__main__':  # It Allows You to Execute Code When the File Runs as a Script
    app.run(host="0.0.0.0",port=5000,debug=True)
