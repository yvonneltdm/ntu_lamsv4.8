function checkNextGateActivity(finishButtonId, toolSessionId, activityId, submitFunction){
	$(document).ready(function(){
		let finishButton = $('#' + finishButtonId);

		if (finishButton.length == 0){
			return;
		}

		let tooltip = new bootstrap.Tooltip(finishButton[0],{
			'trigger' : 'manual',
			// apparently there has to be some title, otherwise the tooltip won't show
			'title' : '-'
		});

		finishButton
			.click(function(event){
				if (finishButton.prop('disabled') == true) {
					// if the button is already disabled, do not run a check
					return;
				}

				// disable the button
				finishButton.prop('disabled', true).attr('disabled', true);

				// check if there is a gate after this activity
				// if so, check if learner can pass
				$.ajax({
					'url' : '/lams/learning/learner/isNextGateActivityOpen.do?toolSessionId=' + toolSessionId + '&activityId=' + activityId,
					'cache' : false,
					'dataType' : 'json',
					'success'  : function(response) {
						if (response.status == 'open') {
							// learner can pass
							finishButton.prop('disabled', false).attr('disabled', false);
							submitFunction();
							return;
						}

						if (response.status == 'closed') {
							// if there are other events bound to click, do not make them run
							event.stopImmediatePropagation();

							let timeoutFunction = null;
							if (response.message) {
								// tooltips should say whatever we got in the response
								tooltip.setContent({'.tooltip-inner' : response.message});
								tooltip.show();

								timeoutFunction = function(){
									tooltip.hide();
									finishButton.prop('disabled', false).attr('disabled', false);
								};
							} else {
								timeoutFunction = function(){
									finishButton.prop('disabled', false).attr('disabled', false);
								};
							}

							// show tooltip for several seconds
							setTimeout(timeoutFunction, 5000);
						}
					}
				});
			});
	});
}