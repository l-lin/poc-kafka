$(function() {
  const totalPoints = 120;
  const updateInterval = 1000;
  const buildEventUrl = (userId) => '/users/' + userId + '/heart-rates/stream';

  // GLOBAL VARIABLES :scream: --------------------------------------------------------------------
  let data = [];
  let heartRate = null;

  // PLOT -----------------------------------------------------------------------------------------
  const buildData = () => {
    if (data.length > 0) {
      data = data.slice(1);
    }

    if (heartRate && !isNaN(heartRate.value)) {
        data.push(heartRate.value);
    }
    while (data.length < totalPoints) {
      data.push(0);
    }

    let res = [];
    for (let i = 0; i < data.length; ++i) {
      res.push([i, data[i]])
    }
    return res;
  }

  let plot = $.plot('.heart-rate-placeholder', [ buildData() ], {
    series: {
      shadowSize: 0  // Drawing is faster without shadows
    },
    yaxis: {
      min: 0,
      max: 250
    },
    xaxis: {
      show: false
    }
  });

  // EVENT SOURCE ---------------------------------------------------------------------------------
  const getUserId = () => {
    let urlParams = new URLSearchParams(window.location.search);
    let userId = urlParams.get('userId');
    if (!userId) {
      userId = 1;
    }
    return userId;
  };

  const userId = getUserId();
  $('.title').text(`Heart rates for user ${userId}`);
  let source = new EventSource(buildEventUrl(userId));
  source.addEventListener('message', (event) => {
    heartRate = JSON.parse(event.data);
    console.info('read heart rate', heartRate);
  });
  source.onerror = () => {
    source.close();
  };

  // INIT -----------------------------------------------------------------------------------------
  const update = () => {
    plot.setData([buildData()]);
    plot.draw();
    setTimeout(update, updateInterval);
  };
  const stop = () => {
    console.info('Stopping event...');
    source.close();
  };

  // START ----------------------------------------------------------------------------------------
  plot.setupGrid();
  update();

  window.onbeforeunload = () => {
    stop();
  };
});
