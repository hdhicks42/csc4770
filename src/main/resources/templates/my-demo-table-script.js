$(document).ready(function() {
 
    $('.jqueryDataTable').dataTable( {
        'bProcessing': false,
        'bServerSide': false,
        'sAjaxSource': './DataServlet',
        'bJQueryUI': true,
        'aoColumns': [
            { 'mData': 'name' },
            { 'mData': 'mark' }
             
        ]
    } ); 
} );