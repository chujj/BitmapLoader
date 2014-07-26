$(function () {

    $('table').footable();

    $('.clear-filter').click(function (e) {
        e.preventDefault();
        $('table.demo').trigger('footable_clear_filter');
	$('.filter-status').val('');
    });

    $('.filter-status').change(function (e) {
        e.preventDefault();
	var filter = $(this).val();
        $('#filter').val('');
        $('table.demo').trigger('footable_filter', {filter: filter});
    });

    $('.get_data').click(function() {
        // $.ajax({
        //     url : 'data.dat',
        //     success : function(data) {
        //         $('table tbody').append(data);
        //         $('table').trigger('footable_redraw');
        //     }
        // });
    });

});


$.ajax({
    url : 'cgi-bin/list_py.cgi?request=' + window.location.host, 
    success : function(data) {
        $('table tbody').append(data);
        $('table').trigger('footable_redraw');
    }
});

