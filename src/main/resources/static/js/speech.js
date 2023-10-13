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
        let translation;

        recognizer.recognizeOnceAsync(
          function (result) {
            startRecognizeOnceAsyncButton.disabled = false;
            if (result.reason === SpeechSDK.ResultReason.TranslatedSpeech) {
              translation = result.translations.get(language);
              window.console.log(translation);
              phraseDiv.innerHTML += translation;
            }

            recognizer.close();
            recognizer = undefined;

            toggleMic();

            $("#sorry-message").hide();
            $("#spinner").show();

            const url = "/?q=" + translation;
            console.log("calling url", url);

            $.get(url, function(data, status) {
                console.log("translation", translation);
                console.log("data", data);
                if (data) {
                    document.location.href= data;
                } else {
                    $("#spinner").hide();
                    $("#sorry-message").show();
                }
            });

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