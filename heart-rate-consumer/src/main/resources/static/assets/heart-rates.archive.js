$(function() {
  const buildUrl = (userId, lastNSeconds) => '/heart-rates/' + userId + '?lastNSeconds=' + lastNSeconds;
  const getQueryParam = (queryParamName, defaultValue) => {
    let urlParams = new URLSearchParams(window.location.search);
    let queryParamValue = urlParams.get(queryParamName);
    if (!queryParamValue) {
      queryParamValue = defaultValue;
    }
    return queryParamValue;
  };
  const userId = getQueryParam('userId', 1);
  const lastNSeconds = getQueryParam('lastNSeconds', 60);
  const heartRatesToFlotData = (heartRates) => {
    let data = [];
    for (let i = 0; i < heartRates.length; i++) {
      let hr = heartRates[i];
      data.push([new Date(hr.timestamp).getTime(), hr.value]);
    }
    return data;
  };

  $('.title').text(`Heart rates for user ${userId} for the last ${lastNSeconds} seconds`);

  const oReq = new XMLHttpRequest();
  oReq.addEventListener('load', function() {
    if (this.status !== 200) {
      console.error('Server did not return the user id. Response status was', this.status);
      return;
    }
    const heartRates = JSON.parse(this.responseText);
    if (heartRates.length === 0) {
      $('.heart-rate-placeholder').text('No heart rate registered during this period');
    } else {
      const data = heartRatesToFlotData(heartRates);
      $.plot('.heart-rate-placeholder', [heartRatesToFlotData(heartRates)], {
        xaxis: {
          autoScale: 'none',
          mode: 'time',
          minTickSize: [1, 'second'],
          min: data[0][0],
          max: data[data.length - 1][0],
          timeBase: 'milliseconds'
        }
      });
    }
  });
  oReq.open('GET', buildUrl(userId, lastNSeconds));
  oReq.send();
});