$(document).ready(function ()
{
    getOrderInfo();

    //$('#btn-print').printPage();
});

function getOrderInfo()
{
    $.ajax(
            {
                type: 'GET',
                url: '/dispatcher/getLunchOrder',
                cache: false,
                async: false,
                dataType: "json",
                success: function (result)
                {
                    //console.log('result: " + result')
                    if (result != null)
                    {
                        var total = result.orderTotal.toFixed(2);
                        var name = result.studentName;
                        var grade = result.studentGrade;
                        var month = result.month;
                        var sortedLunchMap = result.sortedLunchMap;

                        /**
                         * create the confirmation information on the page
                         * @type {Array}
                         */
                        var buffer = [];
                        buffer.push('<h2>' + month + ' Hot Lunch Order Confirmation</h2>');
                        buffer.push('<h3>' + name + '</h3>');
                        if (grade == 'F')
                        {
                            buffer.push('<h3>Faculty</h3>');
                        }
                        else if (grade == 'K')
                        {
                            buffer.push('<h3>Kindergarten</h3>');
                        }
                        else
                        {
                            buffer.push('<h3>Grade: ' + grade + '</h3>');
                        }

                        buffer.push('<h3>Order Total: $' + total + '</h3>');

                        buffer.push('<fieldset>');
                        buffer.push('<table class="table table-bordered table-striped">');
                        buffer.push('<tbody class="body">');
                        // key is the date
                        for (var key in sortedLunchMap)
                        {
                            var order = sortedLunchMap[key];
                            buffer.push('<tr>');
                            buffer.push('<td>' + order.dateString + '</td>');
                            buffer.push('<td>' + getPrettyName(order.itemTypeString) + '</td>');
                            if (order.choice1 != null)
                            {
                                buffer.push('<td>' + getPrettyChoiceName(order.choice1, order.itemTypeString) + '</td>');
                                //buffer.push('<td>' + order.choice1 + '</td>');
                            }
                            else
                            {
                                buffer.push('<td></td>');
                            }
                            if (order.choice2 != null)
                            {
                                buffer.push('<td>' + getPrettyChoiceName(order.choice2, order.itemTypeString) + '</td>');
                            }
                            else
                            {
                                buffer.push('<td></td>');
                            }
                            if (order.orderOne)
                            {
                                buffer.push('<td>Ordered</td>');
                            }
                            if (order.orderAdditional)
                            {
                                buffer.push('<td>Ordered Additional</td>');
                            }
                            buffer.push('</tr>');
                        }
                        buffer.push('</tbody>');
                        buffer.push('</table>');
                        buffer.push('</fieldset>');
                        var bufferString = buffer.join('');

                        // hide the title, lunch-form and submit button
                        $('#lunch-form').hide();
                        $('#submit').hide();
                        $('#title').hide();

                        //console.log('result: ' + bufferString);

                        //$('#btn-print').show();
                        $("#result").append(bufferString);

                        /**
                         * create the bottom section
                         */
                        var bottomBuffer = [];
                        bottomBuffer.push('<p></p><p></p>');
                        bottomBuffer.push('<p class="lead">Please submit the bottom portion of this sheet along with your check to the school</p>')
                        bottomBuffer.push('<h1>-----------------------------------------------------------------------------------</h1>')
                        bottomBuffer.push('<h2>' + month + ' Hot Lunch Order Confirmation</h2>');
                        bottomBuffer.push('<p class="lead">For: ' + name + '</p>');
                        if (grade == 'F')
                        {
                            bottomBuffer.push('<p class="lead">Faculty</p>');
                        }
                        if (grade == 'K')
                        {
                            bottomBuffer.push('<p class="lead">Kindergarten</p>');
                        }
                        else
                        {
                            bottomBuffer.push('<p class="lead">Grade: ' + grade + '</p>');
                        }
                        bottomBuffer.push('<p class="lead">Order Total: $' + total + '</p>');
                        bottomBuffer.push('<p class="lead">Check Number:  ___________</p>')

                        $("#bottom").html(bottomBuffer);
                    }
                    else
                    {
                        alert("We were retrieve your Hot Lunch Order, please try again");
                    }
                }
            });
}

function getCalendarMap()
{
    var map = "";

    $.ajax({
        type: 'GET',
        url: '/dispatcher/getCalendarMap/nov',
        cache: false,
        async: false,
        dataType: "json",
        success: function (data)
        {
            if (data != null)
            {
                //console.log("calendar map: " + data);
                map = data;
            }
        }
    });
    return map;
}

function getOrderedDateMap()
{
    var map = "";

    $.ajax({
        type: 'GET',
        url: '/dispatcher/getOrderedDateMap/nov',
        cache: false,
        async: false,
        dataType: "json",
        success: function (data)
        {
            if (data != null)
            {
                //console.log("date map: " + data);
                map = data;
            }
        }
    });
    return map;
}



