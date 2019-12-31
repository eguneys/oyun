const headers = {
  'Accept': 'application/vnd.oyunkeyf.v2+json'
};

export function get(url, cache) {

  return $.ajax({
    url,
    headers,
    cache
  });

}
