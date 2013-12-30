$(document).ready(function ()
{
    $('#submit').click(function ()
    {
        getOrderInfo();
    });

    $('#print-info').hide();

    // set the typeahead name field
    setUpTypeAhead();
});

function getOrderInfo()
{
    var name = $('#name').val();
    //console.log("name: " + name);
    if (name == "")
    {
        alert("Please Enter a Name");
        $('#name').focus();
        return false;
    }

    var month = $('#month').val();
    if (month == "Select Month")
    {
        alert("Please Select a Month");
        $('#month').focus();
        return false;
    }

    var grade = $('#grade').val();
    //console.log("grade: " + grade);
    if (grade == "Select Grade")
    {
        alert("Please Select a Grade");
        $('#grade').focus();
        return false;
    }

    $.ajax(
            {
                type: 'POST',
                url: '/dispatcher/getLunchOrder',
                data: {month: month, name: name, grade: grade},
                cache: false,
                async: false,
                dataType: "json",
                success: function (result)
                {
                    if (result != null && result.lunchOrderItemMap != null)
                    {

                        var total = result.orderTotal.toFixed(2);
                        var name = result.studentName;
                        var grade = result.studentGrade;
                        var lunchMap = "";
                        if (result.lunchOrderItemMap.orderItemMap == null)
                        {
                            // get the orderItemMap from the orderText String
                            var orderText = result.orderText;
                            //console.log("orderText: " + orderText);
                            var obj = JSON && JSON.parse(orderText) || $.parseJSON(orderText);
                            //console.log("using the orderItemMap from orderText");
                            lunchMap = obj;
                        }
                        else
                        {
                            lunchMap = result.lunchOrderItemMap.orderItemMap;
                            //console.log("using the orderItemMap from lunchOrderItemMap");
                        }

                        /**
                         * sort the lunch map according to date -- date is the key, the place is the value/index
                         * @type {Array}
                         */
                        var calendarMap = getCalendarMap(month); // <String(date), Integer(order)
                        //console.log("calling getOrderedDateMap");
                        var orderedDateMap = getOrderedDateMap(month); // <Integer, String(date)
                        //console.log("month: " + month);
                        //console.log("orderedDateMap: " + orderedDateMap);
                        var orderedLunchMap = new Array();
                        var order = "";
                        var index = "";
                        // key is the date
                        for (var key in lunchMap)
                        {
                            order = lunchMap[key];
                            index = calendarMap[key]; // should be date string
                            orderedLunchMap[index] = order;
                        }

                        /**
                         * create the confirmation information on the page
                         * @type {Array}
                         */
                        var buffer = [];
                        buffer.push('<h2>' + month + ' Hot Lunch Order Summary</h2>');
                        buffer.push('<h3>For: ' + name + '</h3>');
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
                        // key is the date // replace lunchMap with
                        for (var key in orderedLunchMap)
                        {
                            var order = orderedLunchMap[key];
                            buffer.push('<tr>');
                            buffer.push('<td>' + orderedDateMap[key] + '</td>');
                            //buffer.push('<td>' + key + '</td>');
                            buffer.push('<td>' + getPrettyName(order.itemTypeString) + '</td>');
                            if (order.choice1 != null)
                            {
                                buffer.push('<td>' + getPrettyChoiceName(order.choice1, order.itemTypeString) + '</td>');
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
                        $('#print-info').show();
                    }
                    else
                    {
                        alert("We were unable retrieve your Hot Lunch Order, please check the Name and Grade and try again");
                    }
                }
            });
}

function getCalendarMap(month)
{
    var map = "";
    var url = "";
    if (month == "September")
    {
        url = '/dispatcher/getCalendarMap';
    }
    else if (month == "October")
    {
        url = '/dispatcher/getCalendarMap/oct';
    }
    else
    {
        url = '/dispatcher/getCalendarMap/nov';
    }

    $.ajax({
        type: 'GET',
        url: url,
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

function getOrderedDateMap(month)
{
    var map = "";
    var url = "";
    if (month == "September")
    {
        url = '/dispatcher/getOrderedDateMap';
    }
    else if (month == "October")
    {
        url = '/dispatcher/getOrderedDateMap/oct';
    }
    else
    {
        url = '/dispatcher/getOrderedDateMap/nov';
    }
    //console.log("in getOrderedDateMap using URL: " + url);

    $.ajax({
        type: 'GET',
        url: url,
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

