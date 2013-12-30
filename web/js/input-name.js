$(document).ready(function ()
{
    $("#submit").click(function ()
    {
        submitRequest();
    });
});


function submitRequest()
{
    var firstname = $('#first-name').val();
    var lastname = $('#last-name').val();
    var grade = $('#grade').val();

    console.log("first-name: " + firstname);
    console.log("last-name: " + lastname);
    console.log("grade: " + grade);

    $.ajax(
            {
                type: 'POST',
                url: '/dispatcher/addStudentName',
                cache: false,
                async: false,
                data: {
                    firstName: firstname,
                    lastName: lastname,
                    grade: grade
                },
                dataType: "json",
                success: function (result)
                {
                    if (result)
                    {
                        alert("Successfully Added");
                        $('#first-name').val("");
                        $('#last-name').val("");
                        $('#grade').val("Select Grade");
                    }
                    console.log('result: ' + result);
                }
            });


}
