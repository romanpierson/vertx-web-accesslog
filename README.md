# vert-web-accesslog

An access log implementation to be used in vert web routes.

Inspired and with intention to be compliant with

* Apache HTTP Server mod_log_config module

* W3C Extended Log File Format (http://www.w3.org/TR/WD-logfile.html)

## Access Log Pattern Configuration

The logger supports mixing of both log formats and is also designed to easily add custom log elements

## Logging framework

Generating the access log files is performed in a transparent way by vertx logger. Therefore there is any restriction regarding the logging framework used behind (however logback is recommended). Defining rollover strategies are dealt with by the logging framework as well.



