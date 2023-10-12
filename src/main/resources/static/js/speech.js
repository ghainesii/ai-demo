    // status fields and start button in UI
    var phraseDiv;
    var startRecognizeOnceAsyncButton;
    var serviceRegion = "centralus";
    var SpeechSDK;
    var recognizer;

    document.addEventListener("DOMContentLoaded", function () {
      var subscriptionKey = document.getElementById("speechKey");
      startRecognizeOnceAsyncButton = document.getElementById("mic-1");
      phraseDiv = document.getElementById("phraseDiv");

      startRecognizeOnceAsyncButton.addEventListener("click", function () {
        startRecognizeOnceAsyncButton.disabled = true;
        phraseDiv.innerHTML = "";

        var speechConfig = SpeechSDK.SpeechTranslationConfig.fromSubscription(subscriptionKey.value, serviceRegion);

        const language="en";
        speechConfig.speechRecognitionLanguage = "en-US";
        speechConfig.addTargetLanguage(language);


        var audioConfig  = SpeechSDK.AudioConfig.fromDefaultMicrophoneInput();
        recognizer = new SpeechSDK.TranslationRecognizer(speechConfig, audioConfig);

        recognizer.recognizeOnceAsync(
          function (result) {
            startRecognizeOnceAsyncButton.disabled = false;
            if (result.reason === SpeechSDK.ResultReason.TranslatedSpeech) {
              let translation = result.translations.get(language);
              window.console.log(translation);
              phraseDiv.innerHTML += translation;
            }

            recognizer.close();
            recognizer = undefined;

            toggleMic();

          },
          function (err) {
            startRecognizeOnceAsyncButton.disabled = false;
            phraseDiv.innerHTML += err;
            window.console.log(err);

            recognizer.close();
            recognizer = undefined;
          });

      });

      if (!!window.SpeechSDK) {
        SpeechSDK = window.SpeechSDK;
        startRecognizeOnceAsyncButton.disabled = false;
      }
    });