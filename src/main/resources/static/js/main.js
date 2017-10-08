$(function() {
    console.log(new Date());
    var $companyName = $('#companyName');
    if ($companyName.length) {
        var companies = $companyName.data('source').replace(/^'/, '').replace(/'$/, '').split("','");
        var placeholder = 'type name of new company';
        if (companies.length > 0) {
            placeholder += ' or e.g. ' + companies[0];
        }
        if (companies.length > 1) {
            placeholder += ', ' + companies[1];
        }
        if (companies.length > 2) {
            placeholder += ' ...';
        }
        $companyName.attr('placeholder', placeholder).autocomplete({ source: companies });
        $companyName.focus();
    }
    $('.planning-checkbox').each(function() {
        timelineBorder($(this));
    }).on('change', function() {
        timelineBorder($(this));
    });
    $('button:submit').click(function() {
        $(this).parents('form:first').data('submitButton', $(this).prop('value'));
    });
    $('form').submit(function(e) {
        if ($(this).data('submitButton') != 'save') {
            $('#loadingAlert').modal('show');
        } else {
            showSavingProgressAlert();
        }
    });
    $('#savingProgressAlert').on('show.bs.modal', function(e) {
        setInterval(function() {
            $.get('/savingProgress').success(function (data) {
                var val = data.progress;
                var $ProgressBar = $(e.currentTarget).find('.progress-bar');
                $ProgressBar.css('width', val + '%').attr('aria-valuenow', val);
            });
        }, 250);
    });
    $("textarea.auto-grow").each(function(){
        auto_grow(this);
    });
    $('[data-toggle="tooltip"]').tooltip();
    console.log(new Date());
});

function timelineBorder($this) {
    var $column = $this.parent('.planning-column');
    var $label = $column.find('.planning-label');
    var checked = $this.is(':checked');
    var $prevColumn = $column.prev('.planning-column');
    var $prevLabel = $prevColumn.find('.planning-label');
    var prevChecked = $prevColumn.find("input").is(':checked');
    var $nextColumn = $column.next('.planning-column');
    var $nextLabel = $nextColumn.find('.planning-label');
    var nextChecked = $nextColumn.find("input").is(':checked');
    if (checked && prevChecked && nextChecked) {
        $prevLabel.removeClass('planning-end');
        $nextLabel.removeClass('planning-start');
    } else if (checked && prevChecked && !nextChecked) {
        $label.addClass('planning-end');
        $prevLabel.removeClass('planning-end');
    } else if (checked && !prevChecked && nextChecked) {
        $label.addClass('planning-start');
        $nextLabel.removeClass('planning-start');
    } else if (checked && !prevChecked && !nextChecked) {
        $label.addClass('planning-start planning-end');
    } else if (!checked && prevChecked && nextChecked) {
        $prevLabel.addClass('planning-end');
        $nextLabel.addClass('planning-start');
    } else if (!checked && prevChecked && !nextChecked) {
        $label.removeClass('planning-start planning-end');
        $prevLabel.addClass('planning-end');
    } else if (!checked && !prevChecked && nextChecked) {
        $label.removeClass('planning-start planning-end');
        $nextLabel.addClass('planning-start');
    } else if (!checked && !prevChecked && !nextChecked) {
        $label.removeClass('planning-start planning-end');
    }
}

function auto_grow(element) {
    element.style.height = (element.scrollHeight)+"px";
}

function showSavingProgressAlert() {
    $('#savingProgressAlert').modal('show');
}
function goto (nextStep, category, subCategory) {
    var input = $("<input>")
        .attr("type", "hidden")
        .attr("name", "nextStep").val(nextStep);
    $('form').append($(input));

    input = $("<input>")
        .attr("type", "hidden")
        .attr("name", "category").val(category);
    $('form').append($(input));

    input = $("<input>")
        .attr("type", "hidden")
        .attr("name", "subCategory").val(subCategory);
    $('form').append($(input));

    input = $("<input>")
        .attr("type", "hidden")
        .attr("name", "action").val("goto");
    $('form').append($(input))

    //$('form').attr('action','goto');
    $('form').submit();
};
