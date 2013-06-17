$(document).ready(function() {
	var gSteps = {};
	var nextStep = 'x';
	var over = false;
	var to = {};
	to[0] = {
		row : 1,
		col : 0
	};
	to[1] = {
		row : 1,
		col : 1
	};
	to[2] = {
		row : 0,
		col : 1
	};
	to[3] = {
		row : -1,
		col : 1
	};
	var rowNum = 10, colNum = 10;

	function getRows(rowNum, colNum) {
		var rowStr = '', i = 0;
		for (i = 0; i < rowNum; i++) {
			rowStr += '<tr>' + getCell(colNum) + '</tr>\n';
		}
		return rowStr;
	}
	function getCell(time) {
		var cellStr = '', i;
		for (i = 0; i < time; i++) {
			cellStr += '<td class="cellStyle"></td>\n';
		}
		return cellStr;
	}
	function getMaxMatch(sym, row, col, to, needMax) {
		if (row < 0 || col < 0 || row >= rowNum || col >= colNum) {
			return 0;
		}
		if (gSteps[row][col] != sym) {
			return 0;
		}
		if (needMax == 1) {
			return 1;
		}
		return getMaxMatch(sym, row + to.row, col + to.col, to, needMax - 1)
				+ 1;
	}
	function getReverseTo(to) {
		return {
			row : -to.row,
			col : -to.col
		};
	}
	function checkWinner(sym, row, col) {
		for (var i = 0; i < 4; i++) {
			if (getMaxMatch(sym, row, col, to[i], 5)
					+ getMaxMatch(sym, row, col, getReverseTo(to[i]), 5) > 5) {
				return true;
			}
		}
		return false;
	}
	var stepCal = function(event) {
		if (over) {
			return;
		}
		var row = $(event.data).attr("rowIndex");
		var col = $(event.data).attr("colIndex");
		var rowN = parseInt(row);
		var colN = parseInt(col);
		if (gSteps[rowN][colN] != 'm') {
			return;
		}
		gSteps[rowN][colN] = nextStep;
		$(event.data).html(nextStep);
		if (checkWinner(nextStep, rowN, colN)) {
			alert("winner is " + nextStep);
			over = true;
		}
		nextStep = nextStep == 'x' ? 'o' : 'x';
	};
	function reset() {
		for (var i = 0; i < rowNum; i++) {
			gSteps[i] = {};
			for (var j = 0; j < colNum; j++) {
				gSteps[i][j] = 'm';
			}
		}
		$(".cellStyle").each(function(index, element) {
					$(this).html("");
				});
		window.step = stepCal;
		over = false;
		nextStep = 'x';
	}

	window.reset = reset;
	window.step = stepCal;

	$('#con_table').html(getRows(rowNum, colNum));
	$('tr').each(function(index, element) {
				var tdList = element.children;
				for (var i = 0; i < tdList.length; i++) {
					$(tdList[i]).attr("rowIndex", index);
					$(tdList[i]).attr("colIndex", i);
					$(tdList[i]).bind("click", tdList[i], step);
				}
			});
	reset();
});