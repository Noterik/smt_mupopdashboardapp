var WordCloudController = function(options) {}; // needed for detection


WordCloudController.update = function(vars, data){
	// get out targetid from our local vars
	var targetid = data['targetid'];
	var wordcloud = vars['wordcloud'];
	
	if (!wordcloud) {
		    wordcloud = $('#'+targetid).ntkWordcloud();
		    vars['wordcloud'] = wordcloud;
	}
	
	var command = data['command'];
	if (command=="addword") {
    	wordcloud.ntkWordcloud('addWord', {
            text: data['word'],
            fontFamily: data['font']
          })
    }
};