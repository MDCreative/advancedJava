$(document).ready(function(){
    $('.fadeOut').delay(1000).fadeOut(500);
    
    $('#wordsTable tfoot th').each( function () {
        if($(this).index()>0 && $(this).index()<4){
           
        
            var title = $('#wordsTable thead th').eq( $(this).index() ).text();
            $(this).html( '<input type="text" placeholder="Search '+title+'" />' );
        }
    } );
    var table = $('#wordsTable').DataTable();
 
    // Apply the search
    table.columns().every( function () {
        var that = this;
        $( 'input', this.footer() ).on( 'keyup change', function () {
            that
                .search( this.value )
                .draw();
        } );
    } );
});
