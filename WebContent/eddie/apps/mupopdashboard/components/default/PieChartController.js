var PieChartController = function(options) {}; // needed for detection


PieChartController.update = function(vars, data){
	// get out targetid from our local vars
	var targetid = data['targetid'];
	var piechart = vars['piechart'];
	console.log(vars);
	if (!piechart) {
		console.log("INIT PIE CHART");
		piechart = $('#piechart').ntkPieChart({
	        data: []
	      });
		vars['asd'] = "dsa";

		vars['piechart'] = piechart;
	}
	
	var command = data['command'];
	if (command=="addData") {

        piechart.ntkPieChart('setData', data['data']);

		piechart.ntkPieChart('redraw');
    }
};