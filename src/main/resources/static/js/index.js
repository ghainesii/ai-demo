const toggleMic = () => {
    const micIcon = $("#mic-1").find('svg');
    const micOnColor = "rgb(255, 0, 0)";
    const micOffColor = "rgb(0, 0, 0)"
    const color = micIcon.css('stroke');
    if (color === micOnColor) {
        micIcon.css('stroke', micOffColor);
    } else {
        micIcon.css('stroke', micOnColor);
    }
};
$(document).ready(function() {
    $(".mic").click(function() {
        toggleMic();
    });
});