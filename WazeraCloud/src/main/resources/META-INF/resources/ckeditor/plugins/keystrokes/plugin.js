( function() {
    CKEDITOR.plugins.add( 'keystrokes', {
        init: function( editor ) {
            editor.setKeystroke( CKEDITOR.CTRL + 83 /** S */ , 'saveDoc' );
            editor.addCommand( 'saveDoc', {
                exec: function( editor ) {
                	saveDoc();
                }
            } );
        }
    });
} )();