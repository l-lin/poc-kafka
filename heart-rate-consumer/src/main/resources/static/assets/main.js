const fetchUserId = () => {
  return document.getElementById('user-id').value;
};

const checkInRealTime = () => {
  window.location = '/heart-rates.stream.html?userId=' + fetchUserId();
};

const checkArchive = () => {
  window.location = '/heart-rates.archive.html?userId=' + fetchUserId();
};

const appendOpt = (userId, select) => {
  let opt = document.createElement('option');
  opt.value = userId;
  opt.innerHTML = userId;
  select.appendChild(opt);
}

window.onload = () => {
  const userIdsUrl = '/users/ids';
  const oReq = new XMLHttpRequest();
  oReq.addEventListener('load', function() {
    if (this.status !== 200) {
      console.error('Server did not return the user id. Response status was', this.status);
      return;
    }
    const userIds = JSON.parse(this.responseText);
    let userIdSelects = document.getElementsByName('userId');
    for (let i = 0; i < userIds.length; i++) {
      for (let j = 0; j < userIdSelects.length; j++) {
        appendOpt(userIds[i], userIdSelects[j]);
      }
    }
  });
  oReq.open('GET', userIdsUrl);
  oReq.send();
};
