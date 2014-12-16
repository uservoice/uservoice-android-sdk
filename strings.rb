#!/bin/env ruby

require 'net/http'
require 'json'
require 'uri'
require 'nokogiri'

module Strings
  
  def self.update!
    # TODO plurals
    strings = {}
    locales = []

    # load up all the English keys
    File.open("UserVoiceSDK/res/values-en/strings.xml") do |file|
      en_doc = Nokogiri::XML(file)
      en_doc.css("string").each do |string|
        key = string['name']
        strings[key] = {'en' => string.content}
      end
    end

    # load up all the existing translations for those keys
    Dir["UserVoiceSDK/res/values-*/strings.xml"].each do |path|
      locale = path.match(/\/values-([^\/]+)\//)[1]
      next if locale == "en"
      locales << locale
      File.open(path) do |file|
        doc = Nokogiri::XML(file)
        doc.css("string").each do |string|
          key = string['name']
          strings[key][locale] = string.content if strings[key]
        end
      end
    end

    # find missing translations
    strings.each do |key, translations|
      locales.each do |locale|
        next if translations[locale]
        if locale == 'en-GB'
          translations[locale] = translations['en']
        else
          translations[locale] = google_translate(translations['en'], locale)
        end
      end
    end

    # write out all the updated strings files
    locales.each do |locale|
      path = "UserVoiceSDK/res/values-#{locale}/strings.xml"
      doc = nil
      File.open(path) do |file|
        doc = Nokogiri::XML(file) { |config| config.default_xml.noblanks }
        doc.css("string").each do |string|
          key = string['name']
          string.remove unless strings[key]
        end
        strings.each do |key, translations|
          nodes = doc.css("string[name=#{key}]")
          if nodes.empty?
            parent = doc.css("resources")[0]
            node = Nokogiri::XML::Node.new("string", doc)
            node.content = translations[locale]
            node['name'] = key
            parent << node
          else
            nodes[0].content = translations[locale]
          end
        end
      end
      File.open(path, 'w') do |file|
        doc.write_xml_to(file, indent: 4)
      end
    end
  end

  # Google has different keys for certian locales
  def self.google_locale(locale)
    case locale
      when 'zh-rTW'
        'zh-TW'
      when 'nb'
        'no'
      when 'pt-PT'
        'pt'
      else
        locale
    end
  end

  def self.google_translate(string, locale)
    locale = google_locale(locale)

    unless GOOGLE_LANGUAGES[locale]
      puts "Not auto-translated: '#{string}' in #{locale}"
      return nil
    end

    puts "Requesting translation for '#{string}' in #{locale}"

    # wrap any replacements in a <span> so that google won't mess with it
    html_string = string.gsub(/%[@\$\w]+/, "<span rel=\"\\0\"></span>")

    # google translate does not like ampersands
    html_string = html_string.gsub("&", "and")

    html_translation = ''
    begin
      http = Net::HTTP.new('www.googleapis.com', 443)
      http.use_ssl = true
      response = http.get("/language/translate/v2?key=#{GOOGLE_API_KEY}&q=#{URI.encode(html_string)}&source=en&target=#{google_locale(locale)}&format=html")
      json = JSON.parse(response.body)
      if json["data"]
        html_translation = json["data"]["translations"].first["translatedText"]
      elsif json["errors"]
      end
    rescue Exception => e
      puts "\tSkipping '#{string}' (#{e.message})"
      return nil
    end

    html_entities = [['&amp;', '&'],['&quot;', '"'],['&gt;', '>'],['&lt;', '<'],['&#39;',"'"]]
    html_entities.reverse.each {|ent| html_translation.gsub!(ent.first, ent.last) }

    html_translation.gsub(/<span rel="([^"]+)"><\/span>/, "\\1")
  end
end

GOOGLE_LANGUAGES = {
  'af' => 'afrikaans',
  'ar' => 'arabic',
  'be' => 'belarusian',
  'bg' => 'bulgarian',
  'ca' => 'catalan',
  'cs' => 'czech',
  'cy' => 'welsh',
  'da' => 'danish',
  'de' => 'german',
  'el' => 'greek',
  'en' => 'english',
  'es' => 'spanish',
  'et' => 'estonian',
  'fa' => 'persian',
  'fi' => 'finnish',
  'fr' => 'french',
  'ga' => 'irish',
  'gl' => 'galician',
  'hi' => 'hindi',
  'hr' => 'croatian',
  'ht' => 'haitian_creole',
  'hu' => 'hungarian',
  'id' => 'indonesian',
  'is' => 'icelandic',
  'it' => 'italian',
  'iw' => 'hebrew',
  'ja' => 'japanese',
  'ko' => 'korean',
  'lt' => 'lithuanian',
  'lv' => 'latvian',
  'mk' => 'macedonian',
  'ms' => 'malay',
  'mt' => 'maltese',
  'nl' => 'dutch',
  'no' => 'norwegian',
  'pl' => 'polish',
  'pt' => 'portuguese',
  'ro' => 'romanian',
  'ru' => 'russian',
  'sk' => 'slovak',
  'sl' => 'slovenian',
  'sq' => 'albanian',
  'sr' => 'serbian',
  'sv' => 'swedish',
  'sw' => 'swahili',
  'th' => 'thai',
  'tl' => 'filipino',
  'tr' => 'turkish',
  'uk' => 'ukrainian',
  'vi' => 'vietnamese',
  'yi' => 'yiddish',
  'zh-CN' => 'chinese_simplified',
  'zh-TW' => 'chinese_traditional',
}

puts "Paste a google translate api key:"
GOOGLE_API_KEY = gets.strip
if GOOGLE_API_KEY.empty?
  puts "No api key provided."
else
  Strings.update!
end
