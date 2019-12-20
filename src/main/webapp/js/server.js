var http = require('http');
var fs = require("fs");

http.createServer(function(request, response) {
  if(/(.*?).css$/.test(request.url.toString())){
     sendFileContent(response, request.url.toString().substring(1), "text/css");
  }else if(/(.*?).js$/.test(request.url.toString())){
    sendFileContent(response, request.url.toString().substring(1), "text/javascript");
  }else if(/(.*?).html$/.test(request.url.toString())){
    sendFileContent(response, request.url.toString().substring(1), "text/html");
  }else if(request.url.toString().substring(1) == ''){
    sendFileContent(response, "index.html", "text/html");
  }
}).listen(3000);

function sendFileContent(response, fileName, contentType){
  fs.readFile(fileName, function(err, data){
    if(err){
      response.writeHead(404);
      response.write("Not Found!");
    }
    else{
      response.writeHead(200, {'Content-Type': contentType});
      response.write(data);
    }
    response.end();
  });
}