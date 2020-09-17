# dynamo-parallelscan-example
An example of using parallel table scanning in DynamoDB.

## Building the Example
Run the following command to build the example application:

    ./gradlew clean build
    
## Running the Example
Run the following command to start the application with a local DynamoDB instances provided by LocalStack:

    ./gradlew bootRunLocal
    
If successful, you should see something similar to the following in the terminal:

    [segment 0] Found product 00527
    [segment 2] Found product 00327
    [segment 0] Found product 00063
    [segment 2] Found product 00410
    Processed 246 records on segment 0
    [segment 2] Found product 00203
    [segment 2] Found product 00902
    [segment 2] Found product 00847
    [segment 2] Found product 00603
    [segment 2] Found product 00161
    [segment 2] Found product 00091
    [segment 2] Found product 00544
    [segment 2] Found product 00492
    [segment 2] Found product 00729
    [segment 2] Found product 00719
    [segment 2] Found product 00150
    [segment 2] Found product 00429
    [segment 2] Found product 00497
    Processed 247 records on segment 2
    Scan completed!
    
You can run the example multiple times without resetting the Dynamo table. Run the following command to teardown the LocalStack
environment:

    ./gradlew killLocalStack

## Bugs and Feedback
For bugs, questions, and discussions please use the [Github Issues](https://github.com/gregwhitaker/dynamo-parallelscan-example/issues).

## License
MIT License

Copyright (c) 2020-Present Greg Whitaker

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.