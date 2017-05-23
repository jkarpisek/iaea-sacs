$(function() {
    $('.planning-checkbox').each(function() {
        timelineBorder($(this));
    }).on('change', function() {
        timelineBorder($(this));
    });
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